package com.shabanaj.beloyal.features.coupon.controller;

import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.dto.CouponStatusChangeRequest;
import com.shabanaj.beloyal.features.coupon.service.CouponLifecycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/coupons")
@RequiredArgsConstructor
public class CouponLifecycleController {

    private final CouponLifecycleService couponLifecycleService;

    @PatchMapping("/{couponId}/status")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CouponDetailResponse> changeStatus(
            @PathVariable Long businessId,
            @PathVariable Long couponId,
            @RequestBody @Valid CouponStatusChangeRequest request) {
        return ResponseEntity.ok(couponLifecycleService.changeStatus(businessId, couponId, request.getStatus()));
    }

    @PatchMapping("/{couponId}/archive")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Void> archive(
            @PathVariable Long businessId,
            @PathVariable Long couponId) {
        couponLifecycleService.archive(businessId, couponId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{couponId}")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long businessId,
            @PathVariable Long couponId) {
        couponLifecycleService.delete(businessId, couponId);
        return ResponseEntity.noContent().build();
    }
}
