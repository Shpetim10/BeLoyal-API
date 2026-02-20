package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class TokenIsNotValidException extends ApiException {
    public TokenIsNotValidException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
    public TokenIsNotValidException() {
        super(HttpStatus.BAD_REQUEST, "Token is not valid!");
    }
}
