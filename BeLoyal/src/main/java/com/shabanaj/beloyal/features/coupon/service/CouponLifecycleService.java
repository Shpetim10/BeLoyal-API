package com.shabanaj.beloyal.features.coupon.service;

import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.model.Enums.CouponStatus;

public interface CouponLifecycleService {
    CouponDetailResponse changeStatus(Long businessId, Long couponId, CouponStatus newStatus);
    void archive(Long businessId, Long couponId);
    void delete(Long businessId, Long couponId);
}
