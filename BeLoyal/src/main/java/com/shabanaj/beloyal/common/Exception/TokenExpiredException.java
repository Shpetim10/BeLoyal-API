package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends ApiException {
    public TokenExpiredException(String message) {
        super(HttpStatus.LOCKED, message);
    }
}
