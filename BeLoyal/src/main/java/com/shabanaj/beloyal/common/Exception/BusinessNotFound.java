package com.shabanaj.beloyal.common.Exception;

public class BusinessNotFound extends RuntimeException {
    public BusinessNotFound(String message) {
        super(message);
    }
    public BusinessNotFound() {
        super("Business profile was not found!");
    }
}
