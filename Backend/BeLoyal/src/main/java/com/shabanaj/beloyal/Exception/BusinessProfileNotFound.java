package com.shabanaj.beloyal.Exception;

public class BusinessProfileNotFound extends RuntimeException {
    public BusinessProfileNotFound(String message) {
        super(message);
    }
    public BusinessProfileNotFound() {
        super("Business profile was not found!");
    }
}
