package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CatalogItemNotFound extends ApiException {
    public CatalogItemNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
