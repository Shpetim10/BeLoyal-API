package com.shabanaj.beloyal.features.registerLoyaltyPoints.dto;

import com.shabanaj.beloyal.model.Entity.CustomerCoupon;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CouponDiscountResult {
    private Long customerCouponId;
    private BigDecimal originalAmount;
    private BigDecimal discountApplied;
    private BigDecimal finalAmount;
    /** Holds the already-locked CustomerCoupon entity to prevent a second unlocked reload when marking as USED. */
    private CustomerCoupon lockedCustomerCoupon;
}
