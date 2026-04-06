package com.shabanaj.beloyal.features.catalogItems.controller;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemDetailResponse;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemUpdateRequest;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-item/{id}")
@RequiredArgsConstructor
public class UpdateCatalogItemController {

    private final CatalogItemUpdateService catalogItemUpdateService;

    @PatchMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogItemDetailResponse> update(
            @PathVariable("businessId") Long businessId,
            @PathVariable("id") Long id,
            @RequestBody CatalogItemUpdateRequest request) {
        return ResponseEntity.ok(catalogItemUpdateService.update(businessId, id, request));
    }
}
