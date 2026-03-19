package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class BusinessNotActive extends ApiException {
    public BusinessNotActive(String message) {
        super(HttpStatus.LOCKED, message);
    }
}
