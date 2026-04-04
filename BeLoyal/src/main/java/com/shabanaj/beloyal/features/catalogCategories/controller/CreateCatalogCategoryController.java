package com.shabanaj.beloyal.features.catalogCategories.controller;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryCreateRequest;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryCreateResponse;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryCreateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-category")
@RequiredArgsConstructor
public class CreateCatalogCategoryController {
    private final CatalogCategoryCreateService catalogCategoryCreateService;

    @PostMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogCategoryCreateResponse> createCatalogCategory(@PathVariable("businessId") Long businessId, @RequestBody @Valid CatalogCategoryCreateRequest catalogCategoryCreateRequest){
        return ResponseEntity.ok(catalogCategoryCreateService.create(
                businessId,
                catalogCategoryCreateRequest
        ));
    }
}
