package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class LoyaltySettingsNotFound extends ApiException {
    public LoyaltySettingsNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
