package com.shabanaj.beloyal.features.catalogItems.controller;

import com.shabanaj.beloyal.features.catalogItems.dto.CatalogItemStatusChangeResponse;
import com.shabanaj.beloyal.features.catalogItems.service.CatalogItemLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/catalog-item/{id}")
@RequiredArgsConstructor
public class LifecycleCatalogItemController {
    
    private final CatalogItemLifecycleService catalogItemLifecycleService;

    @PatchMapping("/activate")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogItemStatusChangeResponse> activate(@PathVariable("businessId") Long businessId, @PathVariable("id") Long id){
        return ResponseEntity.ok(catalogItemLifecycleService.activate(businessId, id));
    }

    @PatchMapping("/deactivate")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogItemStatusChangeResponse> deactivate(@PathVariable("businessId") Long businessId, @PathVariable("id") Long id){
        return ResponseEntity.ok(catalogItemLifecycleService.deactivate(businessId, id));
    }

    @PatchMapping("/restore")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CatalogItemStatusChangeResponse> restore(@PathVariable("businessId") Long businessId, @PathVariable("id") Long id){
        return ResponseEntity.ok(catalogItemLifecycleService.restore(businessId, id));
    }

    @DeleteMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("businessId") Long businessId, @PathVariable("id") Long id) {
        catalogItemLifecycleService.delete(businessId, id);
        return ResponseEntity.noContent().build();
    }
}
