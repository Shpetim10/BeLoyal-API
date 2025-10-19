package com.shabanaj.beloyal.Exception;

public class UserNotFound extends RuntimeException {
    public UserNotFound() {
        super("User was not found!");
    }
    public UserNotFound(String message) {
        super(message);
    }
}
