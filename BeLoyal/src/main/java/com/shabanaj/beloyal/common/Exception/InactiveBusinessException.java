package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class InactiveBusinessException extends ApiException {
    public InactiveBusinessException(String message) {
        super(HttpStatus.LOCKED, message);
    }
}
