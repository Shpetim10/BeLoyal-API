package com.shabanaj.beloyal.common.Exception;

public class RoleNotAllowedException extends RuntimeException {
    public RoleNotAllowedException() {
        super("A customer cannot have this role!");
    }
}
