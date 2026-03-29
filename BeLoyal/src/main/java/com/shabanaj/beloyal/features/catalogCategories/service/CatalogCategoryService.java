package com.shabanaj.beloyal.features.catalogCategories.service;

import com.shabanaj.beloyal.model.Entity.CatalogCategory;

import java.util.List;

public interface CatalogCategoryService {
    CatalogCategory getCatalogCategoryByIdAndBusinessId(Long id, Long businessId);
    List<CatalogCategory> getCatalogCategoriesByBusinessId(Long businessId);
}
