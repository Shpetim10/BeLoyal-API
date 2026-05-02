package com.shabanaj.beloyal.features.coupon.service;

import com.shabanaj.beloyal.features.coupon.dto.CouponCreateRequest;
import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;

public interface CouponCreateService {
    CouponDetailResponse create(Long businessId, CouponCreateRequest request);
}
