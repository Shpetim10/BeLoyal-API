package com.shabanaj.beloyal.Exception;

public class BusinessNotFound extends RuntimeException {
    public BusinessNotFound(String message) {
        super(message);
    }
    public BusinessNotFound() {
        super("Business profile was not found!");
    }
}
