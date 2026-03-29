package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CatalogCategoryNotFound extends ApiException {
    public CatalogCategoryNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
