package com.shabanaj.beloyal.features.catalogItems.service;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;

import java.util.List;

public interface CatalogItemService {
    void save(CatalogItem catalogItem);
    Integer getNextOrderIndex(Business business, CatalogCategory catalogCategory);
    List<CatalogItem> getCatalogItems(Business business, CatalogCategory catalogCategory);
    CatalogItem getByIdAndBusinessIdAndIsDeletedFalse(Long catalogItemId, Long businessId);
}
