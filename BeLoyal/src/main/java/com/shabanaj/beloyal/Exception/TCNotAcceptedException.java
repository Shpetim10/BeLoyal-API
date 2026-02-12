package com.shabanaj.beloyal.Exception;

public class TCNotAcceptedException extends RuntimeException {
    public TCNotAcceptedException() {
        super("Terms & Conditions are not accepted!");
    }
}
