package com.shabanaj.beloyal.features.customerCoupon.service.impl;

import com.shabanaj.beloyal.common.Exception.*;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.dto.CouponRedeemResponse;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.pointsBucket.service.PointsBucketService;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.features.customerCoupon.dto.CustomerCouponDetailResponse;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CouponRedemptionServiceImplTest {

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
        // Use reflection to set id since Lombok @Builder doesn't expose setId
        setField(business, "id", 1L);

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

    @Test
    void redeem_generatesQrCode() {
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(couponRepository.findWithLockByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(coupon));
        when(customerCouponRepository.countByCouponIdAndCustomerProfileId(anyLong(), anyLong())).thenReturn(0);
        when(loyaltyAccountRepository.findWithLockByCustomerProfileIdAndBusinessId(10L, 1L)).thenReturn(Optional.of(loyaltyAccount));
        when(loyaltyAccountRepository.save(any())).thenReturn(loyaltyAccount);
        when(couponRepository.save(any())).thenReturn(coupon);
        when(pointsTransactionRepository.save(any())).thenReturn(new PointsTransaction());
        ArgumentCaptor<CustomerCoupon> captor = ArgumentCaptor.forClass(CustomerCoupon.class);
        when(customerCouponRepository.save(captor.capture())).thenAnswer(inv -> {
            CustomerCoupon cc = inv.getArgument(0);
            setField(cc, "id", 99L);
            return cc;
        });

        CouponRedeemResponse response = service.redeem(5L, 1L);

        CustomerCoupon saved = captor.getValue();
        assertThat(saved.getQrCode()).isNotBlank();
        assertThat(saved.getQrCodeGeneratedAt()).isNotNull();
        assertThat(response.getQrCode()).isEqualTo(saved.getQrCode());
    }

    @Test
    void redeem_qrCodeIsUniquePerRedemption() {
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(couponRepository.findWithLockByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(coupon));
        when(customerCouponRepository.countByCouponIdAndCustomerProfileId(anyLong(), anyLong())).thenReturn(0);
        when(loyaltyAccountRepository.findWithLockByCustomerProfileIdAndBusinessId(10L, 1L)).thenReturn(Optional.of(loyaltyAccount));
        when(loyaltyAccountRepository.save(any())).thenReturn(loyaltyAccount);
        when(couponRepository.save(any())).thenReturn(coupon);
        when(pointsTransactionRepository.save(any())).thenReturn(new PointsTransaction());

        ArgumentCaptor<CustomerCoupon> captor = ArgumentCaptor.forClass(CustomerCoupon.class);
        when(customerCouponRepository.save(captor.capture())).thenAnswer(inv -> {
            CustomerCoupon cc = inv.getArgument(0);
            setField(cc, "id", 99L);
            return cc;
        });

        service.redeem(5L, 1L);
        String qr1 = captor.getValue().getQrCode();

        // reset loyalty account points for second call
        setField(loyaltyAccount, "availablePoints", 500);
        service.redeem(5L, 1L);
        String qr2 = captor.getValue().getQrCode();

        assertThat(qr1).isNotEqualTo(qr2);
    }

    @Test
    void redeem_throwsCouponNotActive_whenCouponPaused() {
        coupon.setStatus(CouponStatus.PAUSED);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(couponRepository.findWithLockByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(coupon));

        assertThatThrownBy(() -> service.redeem(5L, 1L))
                .isInstanceOf(CouponNotActiveException.class);
    }

    @Test
    void redeem_throwsCouponExpired_whenPastEndDate() {
        coupon.setEndDate(LocalDateTime.now().minusDays(1));
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(couponRepository.findWithLockByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(coupon));

        assertThatThrownBy(() -> service.redeem(5L, 1L))
                .isInstanceOf(CouponExpiredException.class);
    }

    @Test
    void redeem_throwsInsufficientPoints_whenBalanceTooLow() {
        setField(loyaltyAccount, "availablePoints", 50);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(couponRepository.findWithLockByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(coupon));
        when(customerCouponRepository.countByCouponIdAndCustomerProfileId(anyLong(), anyLong())).thenReturn(0);
        when(loyaltyAccountRepository.findWithLockByCustomerProfileIdAndBusinessId(10L, 1L)).thenReturn(Optional.of(loyaltyAccount));

        assertThatThrownBy(() -> service.redeem(5L, 1L))
                .isInstanceOf(InsufficientPointsException.class);
    }

    // ─── wallet / detail DTO contract tests ───────────────────────────────────

    @Test
    void getCustomerCoupons_usedCoupon_isUsedTrueAndCanUseFalse() {
        CustomerCoupon cc = usedCoupon(200L);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(customerCouponRepository.findAllByCustomerProfileIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(cc));
        when(loyaltyAccountRepository.findAllWithBusinessByCustomerProfileId(10L)).thenReturn(List.of());

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        assertThat(result).hasSize(1);
        CustomerCouponDetailResponse dto = result.get(0);
        assertThat(dto.isUsed()).isTrue();
        assertThat(dto.isCanUse()).isFalse();
        assertThat(dto.getCannotUseReason()).isEqualTo("Already used");
        assertThat(dto.getDisplayStatus()).isEqualTo("USED");
    }

    @Test
    void getCustomerCoupons_repeatedPurchases_returnMultipleRowsWithDistinctIds() {
        CustomerCoupon first = redeemedCoupon(301L, "qr-301");
        CustomerCoupon second = redeemedCoupon(302L, "qr-302");
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(customerCouponRepository.findAllByCustomerProfileIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(first, second));
        when(loyaltyAccountRepository.findAllWithBusinessByCustomerProfileId(10L)).thenReturn(List.of());

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CustomerCouponDetailResponse::getCustomerCouponId).containsExactlyInAnyOrder(301L, 302L);
        // couponId must always be the template id
        assertThat(result).extracting(CustomerCouponDetailResponse::getCouponId).containsOnly(5L);
    }

    @Test
    void getCustomerCoupons_discountCoupon_includesDiscountDisplayAndValue() {
        CustomerCoupon cc = discountCoupon(400L, BigDecimal.valueOf(15), null);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(customerCouponRepository.findAllByCustomerProfileIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(cc));
        when(loyaltyAccountRepository.findAllWithBusinessByCustomerProfileId(10L)).thenReturn(List.of());

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        CustomerCouponDetailResponse dto = result.get(0);
        assertThat(dto.getDiscountDisplay()).isEqualTo("15% Off");
        assertThat(dto.getDiscountValue()).isEqualByComparingTo(BigDecimal.valueOf(15));
    }

    @Test
    void getCustomerCoupons_fixedAmountCoupon_includesDiscountDisplayAndValue() {
        CustomerCoupon cc = discountCoupon(401L, null, BigDecimal.valueOf(5));
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(customerCouponRepository.findAllByCustomerProfileIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(cc));
        when(loyaltyAccountRepository.findAllWithBusinessByCustomerProfileId(10L)).thenReturn(List.of());

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        CustomerCouponDetailResponse dto = result.get(0);
        assertThat(dto.getDiscountDisplay()).contains("5").contains("Off");
        assertThat(dto.getDiscountValue()).isEqualByComparingTo(BigDecimal.valueOf(5));
    }

    @Test
    void getCustomerCoupons_freeProductCoupon_includesSnapshotDisplayNames() {
        CustomerCoupon cc = freeProductCouponWithSnapshotNames(500L);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(customerCouponRepository.findAllByCustomerProfileIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(cc));
        when(loyaltyAccountRepository.findAllWithBusinessByCustomerProfileId(10L)).thenReturn(List.of());

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        CustomerCouponDetailResponse dto = result.get(0);
        assertThat(dto.getFreeProductCategory()).isEqualTo("Beverages");
        assertThat(dto.getFreeProductName()).isEqualTo("Espresso");
        assertThat(dto.getFreeProductVariant()).isEqualTo("Large");
        assertThat(dto.getFreeProductQuantity()).isEqualTo(1);
    }

    @Test
    void getCustomerCoupons_activeRedeemedCoupon_canUseTrue() {
        CustomerCoupon cc = redeemedCoupon(600L, "qr-600");
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(customerCouponRepository.findAllByCustomerProfileIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(cc));
        when(loyaltyAccountRepository.findAllWithBusinessByCustomerProfileId(10L)).thenReturn(List.of());

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        CustomerCouponDetailResponse dto = result.get(0);
        assertThat(dto.isCanUse()).isTrue();
        assertThat(dto.getCannotUseReason()).isNull();
        assertThat(dto.isUsed()).isFalse();
    }

    @Test
    void getCustomerCoupons_expiredRedeemedCoupon_canUseFalseWithExpiredReason() {
        CustomerCoupon cc = expiredRedeemedCoupon(700L);
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        // expired rows are NOT filtered by my-coupons (wallet shows history)
        when(customerCouponRepository.findAllByCustomerProfileIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(cc));
        when(loyaltyAccountRepository.findAllWithBusinessByCustomerProfileId(10L)).thenReturn(List.of());

        List<CustomerCouponDetailResponse> result = service.getCustomerCoupons(1L);

        CustomerCouponDetailResponse dto = result.get(0);
        assertThat(dto.isCanUse()).isFalse();
        assertThat(dto.getCannotUseReason()).isEqualTo("Expired");
        assertThat(dto.getDisplayStatus()).isEqualTo("EXPIRED");
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private CustomerCoupon redeemedCoupon(Long id, String qrCode) {
        CustomerCoupon cc = CustomerCoupon.builder()
                .coupon(coupon)
                .business(business)
                .customerProfile(customerProfile)
                .status(CustomerCouponStatus.REDEEMED)
                .redeemedAt(LocalDateTime.now().minusHours(1))
                .expiresAt(LocalDateTime.now().plusDays(7))
                .pointsSpent(100)
                .currency(CurrencyCode.EUR)
                .qrCode(qrCode)
                .snapshotTitle("Free Coffee")
                .snapshotCouponType(CouponType.FREE_PRODUCT)
                .build();
        setField(cc, "id", id);
        return cc;
    }

    private CustomerCoupon usedCoupon(Long id) {
        CustomerCoupon cc = CustomerCoupon.builder()
                .coupon(coupon)
                .business(business)
                .customerProfile(customerProfile)
                .status(CustomerCouponStatus.USED)
                .redeemedAt(LocalDateTime.now().minusDays(2))
                .usedAt(LocalDateTime.now().minusDays(1))
                .expiresAt(LocalDateTime.now().plusDays(5))
                .pointsSpent(100)
                .currency(CurrencyCode.EUR)
                .qrCode("qr-used-" + id)
                .snapshotTitle("Free Coffee")
                .snapshotCouponType(CouponType.FREE_PRODUCT)
                .build();
        setField(cc, "id", id);
        return cc;
    }

    private CustomerCoupon expiredRedeemedCoupon(Long id) {
        CustomerCoupon cc = CustomerCoupon.builder()
                .coupon(coupon)
                .business(business)
                .customerProfile(customerProfile)
                .status(CustomerCouponStatus.REDEEMED)
                .redeemedAt(LocalDateTime.now().minusDays(10))
                .expiresAt(LocalDateTime.now().minusDays(1))
                .pointsSpent(100)
                .currency(CurrencyCode.EUR)
                .qrCode("qr-expired-" + id)
                .snapshotTitle("Free Coffee")
                .snapshotCouponType(CouponType.FREE_PRODUCT)
                .build();
        setField(cc, "id", id);
        return cc;
    }

    private CustomerCoupon discountCoupon(Long id, BigDecimal pct, BigDecimal amt) {
        CouponType type = pct != null ? CouponType.PERCENTAGE_DISCOUNT : CouponType.FIXED_AMOUNT_DISCOUNT;
        LoyaltyCoupon discountTemplate = LoyaltyCoupon.builder()
                .type(type)
                .title("Discount Coupon")
                .pointsCost(50)
                .currency(CurrencyCode.EUR)
                .status(CouponStatus.ACTIVE)
                .visibility(CouponVisibility.PUBLIC)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .totalRedemptions(0)
                .build();
        setField(discountTemplate, "id", 50L);
        setField(discountTemplate, "business", business);

        CustomerCoupon cc = CustomerCoupon.builder()
                .coupon(discountTemplate)
                .business(business)
                .customerProfile(customerProfile)
                .status(CustomerCouponStatus.REDEEMED)
                .redeemedAt(LocalDateTime.now().minusHours(1))
                .expiresAt(LocalDateTime.now().plusDays(7))
                .pointsSpent(50)
                .currency(CurrencyCode.EUR)
                .qrCode("qr-discount-" + id)
                .snapshotTitle("Discount")
                .snapshotCouponType(type)
                .snapshotDiscountPercentage(pct)
                .snapshotDiscountAmount(amt)
                .build();
        setField(cc, "id", id);
        return cc;
    }

    private CustomerCoupon freeProductCouponWithSnapshotNames(Long id) {
        CustomerCoupon cc = CustomerCoupon.builder()
                .coupon(coupon)
                .business(business)
                .customerProfile(customerProfile)
                .status(CustomerCouponStatus.REDEEMED)
                .redeemedAt(LocalDateTime.now().minusHours(1))
                .expiresAt(LocalDateTime.now().plusDays(7))
                .pointsSpent(100)
                .currency(CurrencyCode.EUR)
                .qrCode("qr-fp-" + id)
                .snapshotTitle("Free Coffee")
                .snapshotCouponType(CouponType.FREE_PRODUCT)
                .snapshotFreeProductCategory("Beverages")
                .snapshotFreeProductName("Espresso")
                .snapshotFreeProductVariant("Large")
                .snapshotFreeProductQuantity(1)
                .build();
        setField(cc, "id", id);
        return cc;
    }

    // Utility to set private fields via reflection
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
