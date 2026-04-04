package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CatalogCategoryHasItems extends ApiException {
    public CatalogCategoryHasItems(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
