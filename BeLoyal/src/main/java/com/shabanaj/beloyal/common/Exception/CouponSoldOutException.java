package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CouponSoldOutException extends ApiException {
    public CouponSoldOutException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Coupon has reached its total redemption limit");
    }
}
