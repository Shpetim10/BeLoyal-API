package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class BusinessNotFound extends ApiException {
    public BusinessNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
    public BusinessNotFound() {
        super(HttpStatus.NOT_FOUND, "Business profile was not found!");
    }
}
