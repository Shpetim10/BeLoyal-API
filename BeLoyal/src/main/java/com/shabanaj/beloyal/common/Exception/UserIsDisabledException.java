package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class UserIsDisabledException extends ApiException {
    public UserIsDisabledException() {
        super(HttpStatus.LOCKED, "User is disabled!");
    }
}
