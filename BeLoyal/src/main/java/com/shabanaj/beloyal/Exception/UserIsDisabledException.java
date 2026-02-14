package com.shabanaj.beloyal.Exception;

public class UserIsDisabledException extends RuntimeException {
    public UserIsDisabledException() {
        super("User is disabled!");
    }
}
