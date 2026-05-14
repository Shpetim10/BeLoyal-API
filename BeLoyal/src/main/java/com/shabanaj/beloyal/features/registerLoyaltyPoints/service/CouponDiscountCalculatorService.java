package com.shabanaj.beloyal.features.registerLoyaltyPoints.service;

import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.CouponDiscountResult;
import com.shabanaj.beloyal.model.Entity.CustomerCoupon;

import java.math.BigDecimal;

public interface CouponDiscountCalculatorService {
    CouponDiscountResult calculate(CustomerCoupon customerCoupon, BigDecimal billAmount);
}
