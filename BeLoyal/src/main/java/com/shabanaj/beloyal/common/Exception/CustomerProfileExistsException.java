package com.shabanaj.beloyal.common.Exception;

public class CustomerProfileExistsException extends RuntimeException {
    public CustomerProfileExistsException() {
        super("This user already has a customer profile!");
    }
}
