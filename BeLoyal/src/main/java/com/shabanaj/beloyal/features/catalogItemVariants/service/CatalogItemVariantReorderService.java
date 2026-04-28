package com.shabanaj.beloyal.features.catalogItemVariants.service;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantSummaryResponse;

import java.util.List;

public interface CatalogItemVariantReorderService {

    /**
     * Reorders the variants of a catalog item.
     *
     * <p>Supports both full and partial reorder:
     * <ul>
     *   <li><b>Full reorder</b> — all variants supplied; final order is exactly as specified.</li>
     *   <li><b>Partial reorder</b> — only a subset supplied; listed variants are placed first
     *       (in requested order), remaining variants follow in their current relative order.</li>
     * </ul>
     *
     * @return the full variant list in its new order
     */
    List<CatalogItemVariantSummaryResponse> reorder(Long businessId, Long catalogItemId,
                                                    CatalogItemVariantOrderUpdateRequest request);
}
