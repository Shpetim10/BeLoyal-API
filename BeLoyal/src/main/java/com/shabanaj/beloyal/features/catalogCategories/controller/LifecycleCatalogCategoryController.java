package com.shabanaj.beloyal.features.catalogCategories.controller;

import com.shabanaj.beloyal.features.catalogCategories.dto.CatalogCategoryStatusChangeResponse;
import com.shabanaj.beloyal.features.catalogCategories.service.CatalogCategoryLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-category/{id}")
@RequiredArgsConstructor
public class LifecycleCatalogCategoryController {
    private final CatalogCategoryLifecycleService catalogCategoryLifecycleService;

    @PatchMapping("/activate")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogCategoryStatusChangeResponse> activate(@PathVariable("businessId") Long businessId, @PathVariable("id") Long id){
        return ResponseEntity.ok(catalogCategoryLifecycleService.activate(businessId, id));
    }

    @PatchMapping("/deactivate")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogCategoryStatusChangeResponse> deactivate(@PathVariable("businessId") Long businessId, @PathVariable("id") Long id){
        return ResponseEntity.ok(catalogCategoryLifecycleService.deactivate(businessId, id));
    }

    @PatchMapping("/restore")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogCategoryStatusChangeResponse> restore(@PathVariable("businessId") Long businessId, @PathVariable("id") Long id){
        return ResponseEntity.ok(catalogCategoryLifecycleService.restore(businessId, id));
    }

    @DeleteMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("businessId") Long businessId, @PathVariable("id") Long id) {
        catalogCategoryLifecycleService.delete(businessId, id);
        return ResponseEntity.noContent().build();
    }
}
