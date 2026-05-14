package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CouponAlreadyUsedException extends ApiException {
    public CouponAlreadyUsedException() {
        super(HttpStatus.CONFLICT, "Coupon has already been used");
    }
}
