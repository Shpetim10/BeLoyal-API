package com.shabanaj.beloyal.features.catalogItems.service;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemDetailResponse;

public interface CatalogItemMoveService {
    /**
     * Move a catalog item to a different category.
     * Recompacts order-indices in both the old and new category.
     */
    CatalogItemDetailResponse move(Long businessId, Long itemId, Long newCategoryId);
}
