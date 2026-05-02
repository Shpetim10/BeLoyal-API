package com.shabanaj.beloyal.features.customerCoupon.service;

import com.shabanaj.beloyal.features.customerCoupon.dto.AvailableCouponsResponse;
import com.shabanaj.beloyal.features.customerCoupon.dto.ValidateRedemptionResponse;

public interface CouponAvailabilityService {
    AvailableCouponsResponse getAvailableCoupons(Long businessId, Long userId);
    ValidateRedemptionResponse validateRedemption(Long couponId, Long userId);
}
