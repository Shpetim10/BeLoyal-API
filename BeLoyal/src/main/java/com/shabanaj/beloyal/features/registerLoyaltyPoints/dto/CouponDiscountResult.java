package com.shabanaj.beloyal.features.registerLoyaltyPoints.dto;

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
}
