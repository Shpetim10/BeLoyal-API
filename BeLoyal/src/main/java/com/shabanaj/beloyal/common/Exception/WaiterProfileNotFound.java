package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class WaiterProfileNotFound extends ApiException {
    public WaiterProfileNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
    public WaiterProfileNotFound() {
        super(HttpStatus.NOT_FOUND, "Waiter profile was not found!");
    }
}
