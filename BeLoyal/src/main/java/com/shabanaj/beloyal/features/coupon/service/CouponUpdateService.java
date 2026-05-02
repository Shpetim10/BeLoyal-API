package com.shabanaj.beloyal.features.coupon.service;

import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.dto.CouponUpdateRequest;

public interface CouponUpdateService {
    CouponDetailResponse update(Long businessId, Long couponId, CouponUpdateRequest request);
}
