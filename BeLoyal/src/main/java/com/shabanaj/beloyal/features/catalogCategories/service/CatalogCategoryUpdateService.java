package com.shabanaj.beloyal.features.catalogCategories.service;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryUpdateRequest;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryViewDto;

public interface CatalogCategoryUpdateService {
    CatalogCategoryViewDto update(Long businessId, Long id, CatalogCategoryUpdateRequest request);
}
