package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class InvalidQrCodeException extends ApiException {
    public InvalidQrCodeException() {
        super(HttpStatus.NOT_FOUND, "Invalid or unrecognized QR code");
    }
}
