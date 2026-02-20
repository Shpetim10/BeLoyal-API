package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException() {
        super(HttpStatus.BAD_REQUEST, "Invalid email or password");
    }
}
