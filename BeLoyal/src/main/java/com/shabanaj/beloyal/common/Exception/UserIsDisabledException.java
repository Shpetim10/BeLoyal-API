package com.shabanaj.beloyal.common.Exception;

public class UserIsDisabledException extends RuntimeException {
    public UserIsDisabledException() {
        super("User is disabled!");
    }
}
