package com.shabanaj.beloyal.features.catalogItemVariants.service.impl;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantStatusChangeResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantLifecycleService;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantService;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemService;
import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogItemVariantLifecycleServiceImpl implements CatalogItemVariantLifecycleService {

    private final CatalogItemService catalogItemService;
    private final CatalogItemVariantService catalogItemVariantService;
    private final CatalogItemVariantRepository catalogItemVariantRepository;
    private final CatalogItemVariantOrderHelper orderHelper;

    @Override
    @Transactional
    public CatalogItemVariantStatusChangeResponse activate(Long businessId, Long catalogItemId, Long variantId) {
        CatalogItemVariant variant = resolveVariant(businessId, catalogItemId, variantId);
        variant.setStatus(CatalogStatus.ACTIVE);
        catalogItemVariantRepository.save(variant);
        return toStatusResponse(variant);
    }

    @Override
    @Transactional
    public CatalogItemVariantStatusChangeResponse deactivate(Long businessId, Long catalogItemId, Long variantId) {
        CatalogItemVariant variant = resolveVariant(businessId, catalogItemId, variantId);
        variant.setStatus(CatalogStatus.INACTIVE);
        catalogItemVariantRepository.save(variant);
        return toStatusResponse(variant);
    }

    @Override
    @Transactional
    public void delete(Long businessId, Long catalogItemId, Long variantId) {
        CatalogItemVariant variant = resolveVariant(businessId, catalogItemId, variantId);

        // Soft-delete and clear order slot
        variant.setIsDeleted(true);
        variant.setOrderIndex(null);
        catalogItemVariantRepository.save(variant);

        // Recompact remaining active variants so indices stay gap-free
        orderHelper.recompact(catalogItemId);
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private CatalogItemVariant resolveVariant(Long businessId, Long catalogItemId, Long variantId) {
        // Ensures business ownership of the parent catalog item
        catalogItemService.getByIdAndBusinessIdAndIsDeletedFalse(catalogItemId, businessId);
        return catalogItemVariantService.getByIdAndCatalogItemId(variantId, catalogItemId);
    }

    private CatalogItemVariantStatusChangeResponse toStatusResponse(CatalogItemVariant variant) {
        return CatalogItemVariantStatusChangeResponse.builder()
                .id(variant.getId())
                .status(variant.getStatus().getName())
                .isDeleted(variant.getIsDeleted())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }
}
