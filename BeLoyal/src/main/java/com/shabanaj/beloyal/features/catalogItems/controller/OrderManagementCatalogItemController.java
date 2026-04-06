package com.shabanaj.beloyal.features.catalogItems.controller;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemShortResponse;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemOrderManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-category/{categoryId}/catalog-items")
@RequiredArgsConstructor
public class OrderManagementCatalogItemController {

    private final CatalogItemOrderManagementService catalogItemOrderManagementService;

    /**
     * PATCH /api/besahub/business/{businessId}/catalog-category/{categoryId}/catalog-items/reorder
     *
     * Accepts the new desired positions for items within this category.
     * Returns the full updated list ordered by the new indices.
     */
    @PatchMapping("/reorder")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<List<CatalogItemShortResponse>> updateOrder(
            @PathVariable("businessId") Long businessId,
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody CatalogItemOrderUpdateRequest request) {
        return ResponseEntity.ok(
                catalogItemOrderManagementService.updateOrder(businessId, categoryId, request));
    }
}
