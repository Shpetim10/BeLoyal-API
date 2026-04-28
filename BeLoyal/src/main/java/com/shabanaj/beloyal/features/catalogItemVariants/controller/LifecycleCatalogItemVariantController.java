package com.shabanaj.beloyal.features.catalogItemVariants.controller;

import com.shabanaj.beloyal.features.catalogItemVariants.dto.CatalogItemVariantStatusChangeResponse;
import com.shabanaj.beloyal.features.catalogItemVariants.service.CatalogItemVariantLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-item/{catalogItemId}/variants/{variantId}")
@RequiredArgsConstructor
public class LifecycleCatalogItemVariantController {

    private final CatalogItemVariantLifecycleService catalogItemVariantLifecycleService;

    /**
     * PATCH .../activate
     * Sets the variant status to ACTIVE.
     */
    @PatchMapping("/activate")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogItemVariantStatusChangeResponse> activate(
            @PathVariable("businessId") Long businessId,
            @PathVariable("catalogItemId") Long catalogItemId,
            @PathVariable("variantId") Long variantId) {
        return ResponseEntity.ok(
                catalogItemVariantLifecycleService.activate(businessId, catalogItemId, variantId));
    }

    /**
     * PATCH .../deactivate
     * Sets the variant status to INACTIVE.
     */
    @PatchMapping("/deactivate")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogItemVariantStatusChangeResponse> deactivate(
            @PathVariable("businessId") Long businessId,
            @PathVariable("catalogItemId") Long catalogItemId,
            @PathVariable("variantId") Long variantId) {
        return ResponseEntity.ok(
                catalogItemVariantLifecycleService.deactivate(businessId, catalogItemId, variantId));
    }

    /**
     * DELETE .../
     * Soft-deletes the variant and recompacts the remaining order indices.
     */
    @DeleteMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable("businessId") Long businessId,
            @PathVariable("catalogItemId") Long catalogItemId,
            @PathVariable("variantId") Long variantId) {
        catalogItemVariantLifecycleService.delete(businessId, catalogItemId, variantId);
        return ResponseEntity.noContent().build();
    }
}
