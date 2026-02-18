package com.shabanaj.beloyal.common.Exception;

public class TokenIsNotValidException extends RuntimeException {
    public TokenIsNotValidException(String message) {
        super(message);
    }
    public TokenIsNotValidException() {
        super("Token is not valid!");
    }
}
