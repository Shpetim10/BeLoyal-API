package com.shabanaj.beloyal.features.catalogItemVariants.controller;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantCreateRequest;
import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantCreateResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantCreateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-item/{catalogItemId}/variants")
@RequiredArgsConstructor
public class CatalogItemVariantCreateController {
    private final CatalogItemVariantCreateService catalogItemVariantCreateService;

    @PostMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogItemVariantCreateResponse> create(@PathVariable("businessId") Long businessId,
                                                                   @PathVariable("catalogItemId") Long catalogItemId,
                                                                   @Valid @RequestBody CatalogItemVariantCreateRequest catalogItemVariantCreateRequest) {
        return ResponseEntity.ok(
                catalogItemVariantCreateService.create(businessId ,catalogItemId, catalogItemVariantCreateRequest)
        );
    }
}
