package com.shabanaj.beloyal.common.Exception;

public class UserNotFound extends RuntimeException {
    public UserNotFound() {
        super("User was not found!");
    }
    public UserNotFound(String message) {
        super(message);
    }
}
