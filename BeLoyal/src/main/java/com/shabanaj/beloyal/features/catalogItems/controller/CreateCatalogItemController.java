package com.shabanaj.beloyal.features.catalogItems.controller;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemCreateRequest;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemCreateResponse;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemCreateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-category/{categoryId}/catalog-items")
@RequiredArgsConstructor
public class CreateCatalogItemController {

    private final CatalogItemCreateService catalogItemCreateService;

    @PostMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogItemCreateResponse> create(
            @PathVariable("businessId") Long businessId,
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody CatalogItemCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogItemCreateService.create(businessId, categoryId, request));
    }
}
