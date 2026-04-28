package com.shabanaj.beloyal.features.catalogItemVariants.service;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantCreateRequest;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantCreateResponse;

public interface CatalogItemVariantCreateService {
    CatalogItemVariantCreateResponse create(Long businessId, Long catalogItemId, CatalogItemVariantCreateRequest catalogItemVariantCreateRequest);
}
