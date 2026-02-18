package com.shabanaj.beloyal.common.Exception;

public class WaiterProfileNotFound extends RuntimeException {
    public WaiterProfileNotFound(String message) {
        super(message);
    }
    public WaiterProfileNotFound() {
        super("Waiter profile was not found!");
    }
}
