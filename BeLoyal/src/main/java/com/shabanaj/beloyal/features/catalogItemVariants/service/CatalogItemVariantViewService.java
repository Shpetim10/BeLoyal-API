package com.shabanaj.beloyal.features.catalogItemVariants.service;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantDetailResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantSummaryResponse;

import java.util.List;

public interface CatalogItemVariantViewService {

    /**
     * Returns all active (non-deleted) variants for a catalog item, ordered by
     * {@code orderIndex} ascending.
     */
    List<CatalogItemVariantSummaryResponse> listVariants(Long businessId, Long catalogItemId);

    /**
     * Returns the full details of a single variant that belongs to the given catalog item.
     */
    CatalogItemVariantDetailResponse getVariantDetails(Long businessId, Long catalogItemId, Long variantId);
}
