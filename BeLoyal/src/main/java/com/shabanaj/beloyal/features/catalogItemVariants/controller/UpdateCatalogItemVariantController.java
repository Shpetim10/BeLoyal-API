package com.shabanaj.beloyal.features.catalogItemVariants.controller;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantDetailResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantUpdateRequest;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-item/{catalogItemId}/variants/{variantId}")
@RequiredArgsConstructor
public class UpdateCatalogItemVariantController {

    private final CatalogItemVariantUpdateService catalogItemVariantUpdateService;

    /**
     * PATCH /api/besahub/business/{businessId}/catalog-item/{catalogItemId}/variants/{variantId}
     *
     * Applies a partial update to the variant. Only fields explicitly present in the
     * request body are changed; omitted fields retain their current values.
     */
    @PatchMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogItemVariantDetailResponse> update(
            @PathVariable("businessId") Long businessId,
            @PathVariable("catalogItemId") Long catalogItemId,
            @PathVariable("variantId") Long variantId,
            @RequestBody CatalogItemVariantUpdateRequest request) {
        return ResponseEntity.ok(
                catalogItemVariantUpdateService.update(businessId, catalogItemId, variantId, request));
    }
}
