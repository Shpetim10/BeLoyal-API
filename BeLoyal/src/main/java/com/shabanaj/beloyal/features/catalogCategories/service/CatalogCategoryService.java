package com.shabanaj.beloyal.features.catalogCategories.service;

import com.shabanaj.beloyal.model.Entity.CatalogCategory;

import java.util.List;

public interface CatalogCategoryService {
    void save(CatalogCategory catalogCategory);
    CatalogCategory getCatalogCategoryByIdAndBusinessId(Long id, Long businessId);
    List<CatalogCategory> getCatalogCategoriesByBusinessId(Long businessId);
    boolean hasCategoryWithThisIndex(Long businessId, Integer orderIndex);
    Integer getNextOrderIndex(Long businessId);
    boolean canBeDeleted(Long businessId);
    void delete(CatalogCategory catalogCategory);
}
