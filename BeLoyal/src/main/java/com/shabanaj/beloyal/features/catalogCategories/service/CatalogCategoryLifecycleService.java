package com.shabanaj.beloyal.features.catalogCategories.service;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryStatusChangeResponse;

public interface CatalogCategoryLifecycleService {
    CatalogCategoryStatusChangeResponse activate(Long businessId, Long id);
    CatalogCategoryStatusChangeResponse deactivate(Long businessId, Long id);
    void delete(Long businessId, Long id);
}
