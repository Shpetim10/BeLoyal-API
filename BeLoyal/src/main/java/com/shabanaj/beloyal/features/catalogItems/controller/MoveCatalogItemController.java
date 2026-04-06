package com.shabanaj.beloyal.features.catalogItems.controller;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemDetailResponse;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemMoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-item/{id}")
@RequiredArgsConstructor
public class MoveCatalogItemController {

    private final CatalogItemMoveService catalogItemMoveService;

    @PatchMapping("/move")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogItemDetailResponse> move(
            @PathVariable("businessId") Long businessId,
            @PathVariable("id") Long id,
            @RequestParam("newCategoryId") Long newCategoryId) {
        return ResponseEntity.ok(catalogItemMoveService.move(businessId, id, newCategoryId));
    }
}
