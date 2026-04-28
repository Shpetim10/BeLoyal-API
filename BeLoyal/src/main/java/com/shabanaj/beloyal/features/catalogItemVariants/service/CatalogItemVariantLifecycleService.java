package com.shabanaj.beloyal.features.catalogItemVariants.service;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantStatusChangeResponse;

public interface CatalogItemVariantLifecycleService {

    CatalogItemVariantStatusChangeResponse activate(Long businessId, Long catalogItemId, Long variantId);

    CatalogItemVariantStatusChangeResponse deactivate(Long businessId, Long catalogItemId, Long variantId);

    /**
     * Soft-deletes the variant and recompacts the remaining variants' order indices.
     */
    void delete(Long businessId, Long catalogItemId, Long variantId);
}
