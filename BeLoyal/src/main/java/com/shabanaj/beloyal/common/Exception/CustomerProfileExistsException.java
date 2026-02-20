package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CustomerProfileExistsException extends ApiException {
    public CustomerProfileExistsException() {
        super(HttpStatus.BAD_REQUEST, "This user already has a customer profile!");
    }
}
