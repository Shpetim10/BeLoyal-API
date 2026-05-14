package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CouponNotYetValidException extends ApiException {
    public CouponNotYetValidException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Coupon is not yet valid — it hasn't reached its start date");
    }
}
