package com.shabanaj.beloyal.features.catalogCategories.service;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryViewDto;

import java.util.List;

public interface CatalogCategoryOrderManagementService {
    List<CatalogCategoryViewDto> updateOrder(Long businessId, CatalogCategoryOrderUpdateRequest request);
}
