package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CouponNotActiveException extends ApiException {
    public CouponNotActiveException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Coupon is not active");
    }
}
