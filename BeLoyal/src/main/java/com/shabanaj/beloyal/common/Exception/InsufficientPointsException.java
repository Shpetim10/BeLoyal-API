package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class InsufficientPointsException extends ApiException {
    public InsufficientPointsException(int available, int required) {
        super(HttpStatus.UNPROCESSABLE_ENTITY,
                "Insufficient points. Required: " + required + ", available: " + available);
    }
}
