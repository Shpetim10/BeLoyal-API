package com.shabanaj.beloyal.common.Exception;

public class TCNotAcceptedException extends RuntimeException {
    public TCNotAcceptedException() {
        super("Terms & Conditions are not accepted!");
    }
}
