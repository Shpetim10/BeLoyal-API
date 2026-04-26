package com.shabanaj.beloyal.features.catalogItems.controller;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemDetailResponse;
import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemShortResponse;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/business/{businessId}")
@RequiredArgsConstructor
public class ViewCatalogItemController {
    
    private final CatalogItemViewService catalogItemViewService;

    @GetMapping("/catalog-items")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<List<CatalogItemShortResponse>> getItemsByBusiness(
            @PathVariable("businessId") Long businessId) {
        return ResponseEntity.ok(catalogItemViewService.getItemsByBusiness(businessId));
    }

    @GetMapping("/catalog-category/{categoryId}/catalog-items")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<List<CatalogItemShortResponse>> getItemsByCategory(
            @PathVariable("businessId") Long businessId,
            @PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(catalogItemViewService.getItemsByCategory(businessId, categoryId));
    }

    @GetMapping("/catalog-items/{id}")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<CatalogItemDetailResponse> getItemDetails(
            @PathVariable("businessId") Long businessId,
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(catalogItemViewService.getItemDetails(businessId, id));
    }

    @GetMapping("/catalog-items/trash")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<List<CatalogItemShortResponse>> getTrashItemsByBusiness(
            @PathVariable("businessId") Long businessId) {
        return ResponseEntity.ok(catalogItemViewService.getTrashItemsByBusiness(businessId));
    }
}
