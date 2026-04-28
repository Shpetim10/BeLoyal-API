package com.shabanaj.beloyal.features.catalogItemVariants.controller;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantDetailResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantSummaryResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-item/{catalogItemId}/variants")
@RequiredArgsConstructor
public class ViewCatalogItemVariantController {

    private final CatalogItemVariantViewService catalogItemVariantViewService;

    /**
     * GET /api/besahub/business/{businessId}/catalog-item/{catalogItemId}/variants
     *
     * Returns all active variants for a catalog item, ordered by {@code orderIndex}.
     */
    @GetMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<List<CatalogItemVariantSummaryResponse>> listVariants(
            @PathVariable("businessId") Long businessId,
            @PathVariable("catalogItemId") Long catalogItemId) {
        return ResponseEntity.ok(catalogItemVariantViewService.listVariants(businessId, catalogItemId));
    }

    /**
     * GET /api/besahub/business/{businessId}/catalog-item/{catalogItemId}/variants/{variantId}
     *
     * Returns the full details of a single variant.
     */
    @GetMapping("/{variantId}")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<CatalogItemVariantDetailResponse> getVariantDetails(
            @PathVariable("businessId") Long businessId,
            @PathVariable("catalogItemId") Long catalogItemId,
            @PathVariable("variantId") Long variantId) {
        return ResponseEntity.ok(
                catalogItemVariantViewService.getVariantDetails(businessId, catalogItemId, variantId));
    }
}
