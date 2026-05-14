package com.shabanaj.beloyal.features.customerApis.service;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerCouponDetailDto;

public interface CustomerCouponDetailService {
    CustomerCouponDetailDto getCouponDetail(Long userId, Long couponId);
}
