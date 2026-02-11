package com.shabanaj.beloyal.Exception;

public class TokenIsNotValidException extends RuntimeException {
    public TokenIsNotValidException(String message) {
        super(message);
    }
    public TokenIsNotValidException() {
        super("Token is not valid!");
    }
}
