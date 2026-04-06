package com.shabanaj.beloyal.features.catalogItems.service;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemShortResponse;

import java.util.List;

public interface CatalogItemOrderManagementService {
    /**
     * Apply a client-driven reorder within a category.
     * Validates that every supplied index is unique, non-negative, and
     * within the current item count. Returns the updated list in order.
     */
    List<CatalogItemShortResponse> updateOrder(Long businessId, Long categoryId, CatalogItemOrderUpdateRequest request);
}
