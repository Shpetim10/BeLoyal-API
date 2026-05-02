package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CouponExpiredException extends ApiException {
    public CouponExpiredException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Coupon has expired or is not within its validity period");
    }
}
