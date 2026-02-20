package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class TCNotAcceptedException extends ApiException {
    public TCNotAcceptedException() {
        super(HttpStatus.BAD_REQUEST, "Terms & Conditions are not accepted!");
    }
}
