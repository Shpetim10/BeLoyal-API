package com.shabanaj.beloyal.features.catalogItemVariants.service;

import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;

import java.util.List;

public interface CatalogItemVariantService {
    void save(CatalogItemVariant catalogItemVariant);
    void saveAll(List<CatalogItemVariant> variants);
    List<CatalogItemVariant> getCatalogItemVariantsByCatalogItemIdAndIsDeletedFalse(Long catalogItemId);
    List<CatalogItemVariant> getCatalogItemVariantsByCatalogItemIdAndIsDeletedFalseOrderedByIndex(Long catalogItemId);
    CatalogItemVariant getByIdAndCatalogItemId(Long variantId, Long catalogItemId);
}
