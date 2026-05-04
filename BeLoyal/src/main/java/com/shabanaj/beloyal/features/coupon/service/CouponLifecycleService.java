package com.shabanaj.beloyal.features.coupon.service;

import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.dto.ReviveCouponRequest;
import com.shabanaj.beloyal.model.Enums.CouponStatus;

public interface CouponLifecycleService {
    CouponDetailResponse changeStatus(Long businessId, Long couponId, CouponStatus newStatus);
    void archive(Long businessId, Long couponId);
    void restoreFromArchive(Long businessId, Long couponId);
    void revive(Long businessId, Long couponId, ReviveCouponRequest reviveCouponRequest);
    void delete(Long businessId, Long couponId);
    void restoreFromTrash(Long businessId, Long couponId);
}
