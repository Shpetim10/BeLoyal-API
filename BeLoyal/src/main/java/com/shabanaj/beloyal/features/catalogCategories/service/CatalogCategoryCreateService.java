package com.shabanaj.beloyal.features.catalogCategories.service;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryCreateRequest;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryCreateResponse;

public interface CatalogCategoryCreateService {
    CatalogCategoryCreateResponse create(Long businessId, CatalogCategoryCreateRequest catalogCategoryCreateRequest);
}
