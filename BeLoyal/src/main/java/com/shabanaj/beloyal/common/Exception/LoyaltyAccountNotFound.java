package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class LoyaltyAccountNotFound extends ApiException {
    public LoyaltyAccountNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
