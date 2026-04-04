package com.shabanaj.beloyal.features.catalogCategories.controller;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryOrderUpdateRequest;
import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryViewDto;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryOrderManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-category/reorder")
@RequiredArgsConstructor
public class OrderManagementCatalogCategoryController {

    private final CatalogCategoryOrderManagementService catalogCategoryOrderManagementService;

    @PatchMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<List<CatalogCategoryViewDto>> updateOrder(
            @PathVariable("businessId") Long businessId,
            @RequestBody @Valid CatalogCategoryOrderUpdateRequest request) {
        return ResponseEntity.ok(catalogCategoryOrderManagementService.updateOrder(businessId, request));
    }
}
