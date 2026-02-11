package com.shabanaj.beloyal.Exception;

public class WaiterProfileNotFound extends RuntimeException {
    public WaiterProfileNotFound(String message) {
        super(message);
    }
    public WaiterProfileNotFound() {
        super("Waiter profile was not found!");
    }
}
