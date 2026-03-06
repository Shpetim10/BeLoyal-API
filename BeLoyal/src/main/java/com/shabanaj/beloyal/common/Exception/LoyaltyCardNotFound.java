package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class LoyaltyCardNotFound extends ApiException {
    public LoyaltyCardNotFound(String message) {
        super(HttpStatus.NOT_FOUND , message);
    }
}
