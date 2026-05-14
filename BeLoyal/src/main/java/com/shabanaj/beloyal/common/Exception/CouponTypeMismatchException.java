package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CouponTypeMismatchException extends ApiException {
    public CouponTypeMismatchException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
