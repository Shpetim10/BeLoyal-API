package com.shabanaj.beloyal.features.customerCoupon.service.impl;

import com.shabanaj.beloyal.common.Exception.CouponNotFound;
import com.shabanaj.beloyal.common.Exception.CustomerCouponNotFound;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.dto.CouponRedeemResponse;
import com.shabanaj.beloyal.features.customerCoupon.dto.CustomerCouponDetailResponse;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.pointsBucket.service.PointsBucketService;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CouponRedemptionContractTest {

    @Mock CouponRepository couponRepository;
    @Mock CustomerCouponRepository customerCouponRepository;
    @Mock LoyaltyAccountRepository loyaltyAccountRepository;
    @Mock PointsTransactionRepository pointsTransactionRepository;
    @Mock PointsBucketService pointsBucketService;
    @Mock UserService userService;
    @Mock CustomerProfileService customerProfileService;

    @InjectMocks
    CouponRedemptionServiceImpl service;

    private User user;
    private CustomerProfile customerProfile;
    private Business business;
    private LoyaltyCoupon coupon;
    private LoyaltyAccount loyaltyAccount;

    @BeforeEach
    void setUp() {
        user = new User();
        business = new Business();
        setField(business, "id", 1L);
        setField(business, "businessName", "Test Business");

        customerProfile = new CustomerProfile();
        setField(customerProfile, "id", 10L);
        setField(customerProfile, "user", user);

        coupon = LoyaltyCoupon.builder()
                .type(CouponType.FREE_PRODUCT)
                .title("Free Coffee")
                .pointsCost(100)
                .currency(CurrencyCode.EUR)
                .status(CouponStatus.ACTIVE)
                .visibility(CouponVisibility.PUBLIC)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .totalRedemptions(0)
                .build();
        setField(coupon, "id", 5L);
        setField(coupon, "business", business);

        loyaltyAccount = new LoyaltyAccount();
        setField(loyaltyAccount, "id", 100L);
        setField(loyaltyAccount, "availablePoints", 500);
        setField(loyaltyAccount, "business", business);
        setField(loyaltyAccount, "customerProfile", customerProfile);
    }

    // --- expiresAt set on redemption (rule 4) ---

    @Test
    void redeem_setsExpiresAtFromCouponEndDate() {
        LocalDateTime expectedExpiry = LocalDateTime.now().plusDays(10);
        coupon.setEndDate(expectedExpiry);

        stubRedemptionDependencies();
        ArgumentCaptor<CustomerCoupon> captor = ArgumentCaptor.forClass(CustomerCoupon.class);
        when(customerCouponRepository.save(captor.capture())).thenAnswer(inv -> {
            CustomerCoupon cc = inv.getArgument(0);
            setField(cc, "id", 99L);
            return cc;
        });

        service.redeem(5L, 1L);

        CustomerCoupon saved = captor.getValue();
        assertThat(saved.getExpiresAt()).isNotNull();
        assertThat(saved.getExpiresAt()).isEqualTo(expectedExpiry);
    }

    @Test
    void redeem_rejectsPrivateCouponForCustomer() {
        coupon.setVisibility(CouponVisibility.HIDDEN);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(couponRepository.findWithLockByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(coupon));

        assertThatThrownBy(() -> service.redeem(5L, 1L))
                .isInstanceOf(CouponNotFound.class);
    }

    // --- identity fields in response (rule 1) ---

    @Test
    void getCustomerCoupons_returnsExplicitCouponIdAndCustomerCouponId() {
        CustomerCoupon cc = buildSavedCustomerCoupon(77L, 5L, CustomerCouponStatus.REDEEMED);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(customerCouponRepository.findAllByCustomerProfileIdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(cc));

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        assertThat(result).hasSize(1);
        CustomerCouponDetailResponse r = result.get(0);
        assertThat(r.getCustomerCouponId()).isEqualTo(77L);
        assertThat(r.getId()).isEqualTo(77L);      // backward-compat alias
        assertThat(r.getCouponId()).isEqualTo(5L);
    }

    // --- displayStatus normalization (rule 3) ---

    @Test
    void getCustomerCoupons_redeemedCoupon_returnsActiveDisplayStatus() {
        CustomerCoupon cc = buildSavedCustomerCoupon(77L, 5L, CustomerCouponStatus.REDEEMED);
        setField(cc, "expiresAt", LocalDateTime.now().plusDays(10)); // not expiring soon
        stubGetCoupons(cc);

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        assertThat(result.get(0).getDisplayStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void getCustomerCoupons_redeemedExpiringCoupon_returnsExpiringDisplayStatus() {
        CustomerCoupon cc = buildSavedCustomerCoupon(77L, 5L, CustomerCouponStatus.REDEEMED);
        setField(cc, "expiresAt", LocalDateTime.now().plusHours(12)); // expiring within 3 days
        stubGetCoupons(cc);

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        assertThat(result.get(0).getDisplayStatus()).isEqualTo("EXPIRING");
    }

    @Test
    void getCustomerCoupons_expiredRedeemedCoupon_returnsExpiredDisplayStatus() {
        CustomerCoupon cc = buildSavedCustomerCoupon(77L, 5L, CustomerCouponStatus.REDEEMED);
        setField(cc, "expiresAt", LocalDateTime.now().minusHours(1));
        stubGetCoupons(cc);

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        assertThat(result.get(0).getDisplayStatus()).isEqualTo("EXPIRED");
    }

    @Test
    void getCustomerCoupons_usedCoupon_returnsUsedDisplayStatus() {
        CustomerCoupon cc = buildSavedCustomerCoupon(77L, 5L, CustomerCouponStatus.USED);
        stubGetCoupons(cc);

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        assertThat(result.get(0).getDisplayStatus()).isEqualTo("USED");
    }

    // --- type normalization (rule 2) ---

    @Test
    void getCustomerCoupons_emitsCanonicalTypeName() {
        CustomerCoupon cc = buildSavedCustomerCoupon(77L, 5L, CustomerCouponStatus.REDEEMED);
        setField(cc, "snapshotCouponType", CouponType.PERCENTAGE_DISCOUNT);
        stubGetCoupons(cc);

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        assertThat(result.get(0).getType()).isEqualTo("PERCENTAGE_DISCOUNT");
    }

    // --- owned coupon detail endpoint (rule 5) ---

    @Test
    void getCustomerCouponById_returnsOwnedDetail() {
        CustomerCoupon cc = buildSavedCustomerCoupon(88L, 5L, CustomerCouponStatus.REDEEMED);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(customerCouponRepository.findByIdAndCustomerProfileId(88L, 10L)).thenReturn(Optional.of(cc));

        CustomerCouponDetailResponse result = service.getCustomerCouponById(88L, 1L);

        assertThat(result.getCustomerCouponId()).isEqualTo(88L);
        assertThat(result.getCouponId()).isEqualTo(5L);
    }

    @Test
    void getCustomerCouponById_throwsWhenNotOwnedByCustomer() {
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(customerCouponRepository.findByIdAndCustomerProfileId(88L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCustomerCouponById(88L, 1L))
                .isInstanceOf(CustomerCouponNotFound.class);
    }

    @Test
    void getCustomerCouponsCount_countsOnlyUnexpiredRedeemedCoupons() {
        when(customerCouponRepository.countActiveByCustomerProfile(
                eq(customerProfile), eq(CustomerCouponStatus.REDEEMED), any(LocalDateTime.class)))
                .thenReturn(2L);

        assertThat(service.getCustomerCouponsCount(customerProfile)).isEqualTo(2);

        verify(customerCouponRepository).countActiveByCustomerProfile(
                eq(customerProfile), eq(CustomerCouponStatus.REDEEMED), any(LocalDateTime.class));
    }

    // --- helpers ---

    private CustomerCoupon buildSavedCustomerCoupon(Long ccId, Long couponId, CustomerCouponStatus status) {
        LoyaltyCoupon c = LoyaltyCoupon.builder()
                .type(CouponType.FREE_PRODUCT)
                .title("Free Coffee")
                .pointsCost(100)
                .currency(CurrencyCode.EUR)
                .status(CouponStatus.ACTIVE)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .totalRedemptions(0)
                .build();
        setField(c, "id", couponId);
        setField(c, "business", business);

        CustomerCoupon cc = CustomerCoupon.builder()
                .coupon(c)
                .business(business)
                .customerProfile(customerProfile)
                .status(status)
                .pointsSpent(100)
                .currency(CurrencyCode.EUR)
                .redeemedAt(LocalDateTime.now().minusDays(1))
                .qrCode("test-qr-" + ccId)
                .snapshotTitle("Free Coffee")
                .snapshotCouponType(CouponType.FREE_PRODUCT)
                .build();
        setField(cc, "id", ccId);
        setField(cc, "createdAt", LocalDateTime.now().minusDays(1));
        return cc;
    }

    private void stubGetCoupons(CustomerCoupon cc) {
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(customerCouponRepository.findAllByCustomerProfileIdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(cc));
    }

    private void stubRedemptionDependencies() {
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(couponRepository.findWithLockByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(coupon));
        when(customerCouponRepository.countByCouponIdAndCustomerProfileId(anyLong(), anyLong())).thenReturn(0);
        when(loyaltyAccountRepository.findWithLockByCustomerProfileIdAndBusinessId(10L, 1L))
                .thenReturn(Optional.of(loyaltyAccount));
        when(loyaltyAccountRepository.save(any())).thenReturn(loyaltyAccount);
        when(couponRepository.save(any())).thenReturn(coupon);
        when(pointsTransactionRepository.save(any())).thenReturn(new PointsTransaction());
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }

    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }
}
