package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class IdempotencyAlreadyProcessingException extends ApiException {
    public IdempotencyAlreadyProcessingException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
