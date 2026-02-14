package com.shabanaj.beloyal.Exception;

public class UserIsLockedException extends RuntimeException {
    public UserIsLockedException() {
        super("User is temporarily locked!");
    }
}
