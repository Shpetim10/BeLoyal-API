package com.shabanaj.beloyal.Exception;

public class CustomerProfileExistsException extends RuntimeException {
    public CustomerProfileExistsException() {
        super("This user already has a customer profile!");
    }
}
