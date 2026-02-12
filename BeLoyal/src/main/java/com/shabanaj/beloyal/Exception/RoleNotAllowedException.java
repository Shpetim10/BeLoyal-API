package com.shabanaj.beloyal.Exception;

public class RoleNotAllowedException extends RuntimeException {
    public RoleNotAllowedException() {
        super("A customer cannot have this role!");
    }
}
