package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class UserNotFound extends ApiException {
    public UserNotFound() {
        super(HttpStatus.NOT_FOUND, "User was not found!");
    }
    public UserNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
