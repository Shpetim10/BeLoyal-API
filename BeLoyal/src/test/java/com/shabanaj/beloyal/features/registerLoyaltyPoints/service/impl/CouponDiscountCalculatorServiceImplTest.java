package com.shabanaj.beloyal.features.registerLoyaltyPoints.service.impl;

import com.shabanaj.beloyal.common.Exception.CouponExpiredException;
import com.shabanaj.beloyal.common.Exception.InvalidCouponOperationException;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.CouponDiscountResult;
import com.shabanaj.beloyal.model.Entity.CouponDiscountDetails;
import com.shabanaj.beloyal.model.Entity.CustomerCoupon;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class CouponDiscountCalculatorServiceImplTest {

    private final CouponDiscountCalculatorServiceImpl service = new CouponDiscountCalculatorServiceImpl();

    @Test
    void percentageDiscountRespectsMinimumOrderAmount() {
        CustomerCoupon customerCoupon = discountCoupon(
                CouponType.PERCENTAGE_DISCOUNT,
                new BigDecimal("10"),
                null,
                new BigDecimal("50"),
                null,
                LocalDateTime.now().plusDays(3));

        assertThatThrownBy(() -> service.calculate(customerCoupon, new BigDecimal("40")))
                .isInstanceOf(InvalidCouponOperationException.class)
                .hasMessageContaining("below the minimum required");
    }

    @Test
    void percentageDiscountStillAppliesMaximumDiscountCap() {
        CustomerCoupon customerCoupon = discountCoupon(
                CouponType.PERCENTAGE_DISCOUNT,
                new BigDecimal("25"),
                null,
                new BigDecimal("50"),
                new BigDecimal("30"),
                LocalDateTime.now().plusDays(3));

        CouponDiscountResult result = service.calculate(customerCoupon, new BigDecimal("200"));

        assertThat(result.getDiscountApplied()).isEqualByComparingTo("30");
        assertThat(result.getFinalAmount()).isEqualByComparingTo("170");
    }

    @Test
    void discountApplicationUsesCustomerCouponSnapshotExpiry() {
        CustomerCoupon customerCoupon = discountCoupon(
                CouponType.FIXED_AMOUNT_DISCOUNT,
                null,
                new BigDecimal("10"),
                null,
                null,
                LocalDateTime.now().minusHours(1));
        customerCoupon.getCoupon().setEndDate(LocalDateTime.now().plusDays(10));

        assertThatThrownBy(() -> service.calculate(customerCoupon, new BigDecimal("100")))
                .isInstanceOf(CouponExpiredException.class);
    }

    private CustomerCoupon discountCoupon(CouponType type, BigDecimal percentage, BigDecimal amount,
                                          BigDecimal minimumOrderAmount, BigDecimal maximumDiscountAmount,
                                          LocalDateTime expiresAt) {
        CouponDiscountDetails details = CouponDiscountDetails.builder()
                .discountPercentage(percentage)
                .discountAmount(amount)
                .minimumOrderAmount(minimumOrderAmount)
                .maximumDiscountAmount(maximumDiscountAmount)
                .build();

        LoyaltyCoupon coupon = LoyaltyCoupon.builder()
                .type(type)
                .status(CouponStatus.ACTIVE)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .discountDetails(details)
                .build();

        CustomerCoupon customerCoupon = CustomerCoupon.builder()
                .coupon(coupon)
                .status(CustomerCouponStatus.REDEEMED)
                .expiresAt(expiresAt)
                .snapshotDiscountPercentage(percentage)
                .snapshotDiscountAmount(amount)
                .snapshotMinimumOrderAmount(minimumOrderAmount)
                .snapshotMaximumDiscountAmount(maximumDiscountAmount)
                .build();
        setField(customerCoupon, "id", 99L);
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
