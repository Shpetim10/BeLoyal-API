package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class IdempotencyConflictException extends ApiException {
    public IdempotencyConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
