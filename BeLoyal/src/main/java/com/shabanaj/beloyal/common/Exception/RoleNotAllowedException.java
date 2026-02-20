package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class RoleNotAllowedException extends ApiException {
    public RoleNotAllowedException() {
        super(HttpStatus.BAD_REQUEST, "A customer cannot have this role!");
    }
}
