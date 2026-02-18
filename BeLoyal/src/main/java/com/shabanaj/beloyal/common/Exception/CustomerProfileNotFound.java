package com.shabanaj.beloyal.common.Exception;

public class CustomerProfileNotFound extends RuntimeException {
    public CustomerProfileNotFound(String message) {
        super(message);
    }
    public CustomerProfileNotFound() {
        super("Customer profile was not found!");
    }
}
