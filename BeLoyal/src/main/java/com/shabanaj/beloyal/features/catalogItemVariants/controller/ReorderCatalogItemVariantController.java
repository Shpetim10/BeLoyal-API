package com.shabanaj.beloyal.features.catalogItemVariants.controller;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantSummaryResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantReorderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-item/{catalogItemId}/variants")
@RequiredArgsConstructor
public class ReorderCatalogItemVariantController {

    private final CatalogItemVariantReorderService catalogItemVariantReorderService;

    /**
     * PATCH /api/besahub/business/{businessId}/catalog-item/{catalogItemId}/variants/reorder
     *
     * Accepts the new desired positions for variants within this catalog item.
     * Supports both full and partial reorder (see service javadoc).
     * Returns the full updated variant list ordered by the new indices.
     */
    @PatchMapping("/reorder")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<List<CatalogItemVariantSummaryResponse>> reorder(
            @PathVariable("businessId") Long businessId,
            @PathVariable("catalogItemId") Long catalogItemId,
            @Valid @RequestBody CatalogItemVariantOrderUpdateRequest request) {
        return ResponseEntity.ok(
                catalogItemVariantReorderService.reorder(businessId, catalogItemId, request));
    }
}
