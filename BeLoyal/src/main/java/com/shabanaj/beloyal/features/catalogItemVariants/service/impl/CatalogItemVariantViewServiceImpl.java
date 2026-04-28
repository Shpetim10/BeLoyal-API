package com.shabanaj.beloyal.features.catalogItemVariants.service.impl;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantDetailResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantSummaryResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantService;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantViewService;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemService;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogItemVariantViewServiceImpl implements CatalogItemVariantViewService {

    private final CatalogItemService catalogItemService;
    private final CatalogItemVariantService catalogItemVariantService;

    @Override
    public List<CatalogItemVariantSummaryResponse> listVariants(Long businessId, Long catalogItemId) {
        // Validate catalog item belongs to the business
        catalogItemService.getByIdAndBusinessIdAndIsDeletedFalse(catalogItemId, businessId);

        return catalogItemVariantService
                .getCatalogItemVariantsByCatalogItemIdAndIsDeletedFalseOrderedByIndex(catalogItemId)
                .stream()
                .map(CatalogItemVariantViewServiceImpl::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CatalogItemVariantDetailResponse getVariantDetails(Long businessId, Long catalogItemId, Long variantId) {
        // Validate catalog item belongs to the business
        CatalogItem catalogItem = catalogItemService.getByIdAndBusinessIdAndIsDeletedFalse(catalogItemId, businessId);

        CatalogItemVariant variant = catalogItemVariantService.getByIdAndCatalogItemId(variantId, catalogItemId);

        return toDetailResponse(variant, catalogItem);
    }

    // ── mappers ────────────────────────────────────────────────────────────────

    static CatalogItemVariantSummaryResponse toSummaryResponse(CatalogItemVariant variant) {
        return CatalogItemVariantSummaryResponse.builder()
                .id(variant.getId())
                .name(variant.getName())
                .description(variant.getDescription())
                .priceOverride(variant.getPriceOverride())
                .status(variant.getStatus().getName())
                .orderIndex(variant.getOrderIndex())
                .build();
    }

    static CatalogItemVariantDetailResponse toDetailResponse(CatalogItemVariant variant, CatalogItem catalogItem) {
        return CatalogItemVariantDetailResponse.builder()
                .id(variant.getId())
                .catalogItemId(catalogItem.getId())
                .name(variant.getName())
                .description(variant.getDescription())
                .priceOverride(variant.getPriceOverride())
                .status(variant.getStatus().getName())
                .orderIndex(variant.getOrderIndex())
                .isDeleted(variant.getIsDeleted())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }
}
