package com.shabanaj.beloyal.features.couponLookup.controller;

import com.shabanaj.beloyal.features.couponLookup.dto.CategoryLookupItem;
import com.shabanaj.beloyal.features.couponLookup.dto.ProductLookupItem;
import com.shabanaj.beloyal.features.couponLookup.dto.VariantLookupItem;
import com.shabanaj.beloyal.features.couponLookup.service.CouponLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/lookups")
@RequiredArgsConstructor
public class CouponLookupController {

    private final CouponLookupService couponLookupService;

    @GetMapping("/categories")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, List<CategoryLookupItem>>> getActiveCategories(
            @PathVariable Long businessId) {
        return ResponseEntity.ok(Map.of("data", couponLookupService.getActiveCategories(businessId)));
    }

    @GetMapping("/products")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, List<ProductLookupItem>>> getActiveProductsByCategory(
            @PathVariable Long businessId,
            @RequestParam Long categoryId) {
        return ResponseEntity.ok(Map.of("data", couponLookupService.getActiveProductsByCategory(businessId, categoryId)));
    }

    @GetMapping("/products/{productId}/variants")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, List<VariantLookupItem>>> getActiveVariantsByProduct(
            @PathVariable Long businessId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(Map.of("data", couponLookupService.getActiveVariantsByProduct(businessId, productId)));
    }
}
