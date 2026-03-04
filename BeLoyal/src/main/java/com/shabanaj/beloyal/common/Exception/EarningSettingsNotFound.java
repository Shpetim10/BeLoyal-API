package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class EarningSettingsNotFound extends ApiException {
    public EarningSettingsNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
