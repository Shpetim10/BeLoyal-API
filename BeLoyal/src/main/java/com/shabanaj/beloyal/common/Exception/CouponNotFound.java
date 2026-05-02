package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CouponNotFound extends ApiException {
    public CouponNotFound(Long couponId) {
        super(HttpStatus.NOT_FOUND, "Coupon not found: " + couponId);
    }
}
