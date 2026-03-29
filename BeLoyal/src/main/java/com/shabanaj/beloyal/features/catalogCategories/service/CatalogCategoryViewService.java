package com.shabanaj.beloyal.features.catalogCategories.service;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryViewDto;

import java.util.List;

public interface CatalogCategoryViewService {
    CatalogCategoryViewDto viewCatalogCategory(Long id, Long businessId);
    List<CatalogCategoryViewDto> viewCatalogCategories(Long businessId);
}
