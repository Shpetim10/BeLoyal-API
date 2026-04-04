package com.shabanaj.beloyal.features.catalogCategories.controller;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryUpdateRequest;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryViewDto;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-category/{id}")
@RequiredArgsConstructor
public class UpdateCatalogCategoryController {

    private final CatalogCategoryUpdateService catalogCategoryUpdateService;

    @PatchMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogCategoryViewDto> update(
            @PathVariable("businessId") Long businessId,
            @PathVariable("id") Long id,
            @RequestBody @Valid CatalogCategoryUpdateRequest request) {
        return ResponseEntity.ok(catalogCategoryUpdateService.update(businessId, id, request));
    }
}
