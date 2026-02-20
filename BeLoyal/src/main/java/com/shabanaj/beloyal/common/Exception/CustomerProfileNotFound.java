package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CustomerProfileNotFound extends ApiException {
    public CustomerProfileNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
    public CustomerProfileNotFound() {
        super(HttpStatus.NOT_FOUND, "Customer profile was not found!");
    }
}
