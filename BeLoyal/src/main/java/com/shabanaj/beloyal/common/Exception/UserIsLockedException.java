package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class UserIsLockedException extends ApiException {
    public UserIsLockedException() {
        super(HttpStatus.LOCKED, "User is temporarily locked!");
    }
}
