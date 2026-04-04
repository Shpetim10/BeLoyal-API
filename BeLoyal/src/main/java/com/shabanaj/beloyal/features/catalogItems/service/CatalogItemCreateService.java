package com.shabanaj.beloyal.features.catalogItems.service;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemCreateRequest;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemCreateResponse;

public interface CatalogItemCreateService {
    CatalogItemCreateResponse create(Long businessId, Long categoryId, CatalogItemCreateRequest catalogItemCreateRequest);
}
