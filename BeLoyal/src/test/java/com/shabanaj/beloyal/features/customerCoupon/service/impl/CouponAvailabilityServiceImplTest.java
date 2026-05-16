package com.shabanaj.beloyal.features.customerCoupon.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.dto.ValidateRedemptionResponse;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CouponVisibility;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponAvailabilityServiceImplTest {

    @Mock CouponRepository couponRepository;
    @Mock BusinessService businessService;
    @Mock UserService userService;
    @Mock CustomerProfileService customerProfileService;
    @Mock LoyaltyAccountRepository loyaltyAccountRepository;
    @Mock CustomerCouponRepository customerCouponRepository;

    @InjectMocks
    CouponAvailabilityServiceImpl service;

    private User user;
    private CustomerProfile customerProfile;
    private LoyaltyCoupon coupon;

    @BeforeEach
    void setUp() {
        user = new User();
        customerProfile = new CustomerProfile();

        Business business = new Business();
        setField(business, "id", 1L);

        coupon = LoyaltyCoupon.builder()
                .type(CouponType.FREE_PRODUCT)
                .title("Private reward")
                .pointsCost(10)
                .currency(CurrencyCode.EUR)
                .status(CouponStatus.ACTIVE)
                .visibility(CouponVisibility.HIDDEN)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .build();
        setField(coupon, "id", 5L);
        setField(coupon, "business", business);
    }

    @Test
    void validateRedemptionRejectsNonPublicCoupons() {
        when(userService.getUserOrThrow(1L)).thenReturn(user);
        when(customerProfileService.getCustomerProfileByUser(user)).thenReturn(customerProfile);
        when(couponRepository.findById(5L)).thenReturn(Optional.of(coupon));

        ValidateRedemptionResponse response = service.validateRedemption(5L, 1L);

        assertThat(response.isCanRedeem()).isFalse();
        assertThat(response.getReason()).isEqualTo("Coupon is not active");
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
