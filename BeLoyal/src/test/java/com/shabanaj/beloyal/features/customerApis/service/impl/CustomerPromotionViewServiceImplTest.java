package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerPromotionDto;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CustomerCoupon;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerPromotionViewServiceImplTest {

    @Mock UserService userService;
    @Mock CustomerProfileService customerProfileService;
    @Mock CustomerCouponRepository customerCouponRepository;
    @Mock CouponRepository couponRepository;
    @Mock LoyaltyAccountRepository loyaltyAccountRepository;

    @InjectMocks
    CustomerPromotionViewServiceImpl service;

    private User user;
    private CustomerProfile customerProfile;
    private Business business;
    private LoyaltyCoupon coupon;

    @BeforeEach
    void setUp() {
        user = new User();
        customerProfile = new CustomerProfile();
        setField(customerProfile, "id", 10L);

        business = new Business();
        setField(business, "id", 1L);
        setField(business, "businessName", "Cafe");

        coupon = LoyaltyCoupon.builder()
                .type(CouponType.FREE_PRODUCT)
                .title("Coffee")
                .description("Free coffee")
                .pointsCost(20)
                .currency(CurrencyCode.EUR)
                .status(CouponStatus.ACTIVE)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .totalRedemptions(2)
                .build();
        setField(coupon, "id", 5L);
        setField(coupon, "business", business);
    }

    @Test
    void publicPromotionRow_hasUsageCountZeroNotTotalRedemptions() {
        // coupon has totalRedemptions=2 (other customers), but for this unowned public row usageCount must be 0
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(loyaltyAccountRepository.findAllWithBusinessByCustomerProfileId(10L)).thenReturn(List.of());
        when(customerCouponRepository.findAllWithCouponByCustomerProfileId(10L)).thenReturn(List.of());
        when(couponRepository.findAllActivePublic(any(LocalDateTime.class))).thenReturn(List.of(coupon));

        List<CustomerPromotionDto> result = service.getPromotions(1L, null);

        assertThat(result).hasSize(1);
        CustomerPromotionDto dto = result.get(0);
        assertThat(dto.isOwned()).isFalse();
        assertThat(dto.usageCount()).isZero();
        assertThat(dto.totalRedemptions()).isEqualTo(2);
    }

    @Test
    void promotionsPreserveMultipleOwnedCopiesOfSameCouponTemplate() {
        CustomerCoupon first = ownedCoupon(101L, "qr-101");
        CustomerCoupon second = ownedCoupon(102L, "qr-102");

        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(loyaltyAccountRepository.findAllWithBusinessByCustomerProfileId(10L)).thenReturn(List.of());
        when(customerCouponRepository.findAllWithCouponByCustomerProfileId(10L)).thenReturn(List.of(first, second));
        when(couponRepository.findAllActivePublic(any(LocalDateTime.class))).thenReturn(List.of());

        List<CustomerPromotionDto> result = service.getPromotions(1L, null);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CustomerPromotionDto::id).containsExactly(101L, 102L);
        assertThat(result).extracting(CustomerPromotionDto::qrCode).containsExactly("qr-101", "qr-102");
    }

    private CustomerCoupon ownedCoupon(Long id, String qrCode) {
        CustomerCoupon customerCoupon = CustomerCoupon.builder()
                .coupon(coupon)
                .business(business)
                .customerProfile(customerProfile)
                .status(CustomerCouponStatus.REDEEMED)
                .redeemedAt(LocalDateTime.now().minusDays(1))
                .expiresAt(LocalDateTime.now().plusDays(5))
                .pointsSpent(20)
                .currency(CurrencyCode.EUR)
                .qrCode(qrCode)
                .snapshotTitle("Coffee")
                .snapshotDescription("Free coffee")
                .snapshotCouponType(CouponType.FREE_PRODUCT)
                .build();
        setField(customerCoupon, "id", id);
        return customerCoupon;
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
