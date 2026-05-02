package com.shabanaj.beloyal.features.coupon.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CouponDiscountDetailsDto {
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal minimumOrderAmount;
    private BigDecimal maximumDiscountAmount;
}
