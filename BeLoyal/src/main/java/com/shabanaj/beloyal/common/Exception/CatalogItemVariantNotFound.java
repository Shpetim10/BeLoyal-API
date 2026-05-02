package com.shabanaj.beloyal.common.Exception;

import org.springframework.http.HttpStatus;

public class CatalogItemVariantNotFound extends ApiException {
    public CatalogItemVariantNotFound(Long variantId) {
        super(HttpStatus.NOT_FOUND, "Catalog item variant not found: " + variantId);
    }
}
