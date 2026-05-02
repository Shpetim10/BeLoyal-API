package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class InvalidCouponOperationException extends ApiException {
    public InvalidCouponOperationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
