package com.shabanaj.beloyal.features.catalogItemVariants.service;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantDetailResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantUpdateRequest;

public interface CatalogItemVariantUpdateService {

    /**
     * Applies a partial PATCH update to a catalog item variant.
     * Only fields that are explicitly present in the request are changed.
     *
     * @return the updated variant detail
     */
    CatalogItemVariantDetailResponse update(Long businessId, Long catalogItemId, Long variantId,
                                            CatalogItemVariantUpdateRequest request);
}
