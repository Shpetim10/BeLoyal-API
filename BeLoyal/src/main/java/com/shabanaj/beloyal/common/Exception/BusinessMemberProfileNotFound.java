package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class BusinessMemberProfileNotFound extends ApiException {
    public BusinessMemberProfileNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
    public BusinessMemberProfileNotFound() {
        super(HttpStatus.NOT_FOUND, "Waiter profile was not found!");
    }
}
