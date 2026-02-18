package com.shabanaj.beloyal.common.Exception;

public class UserIsLockedException extends RuntimeException {
    public UserIsLockedException() {
        super("User is temporarily locked!");
    }
}
