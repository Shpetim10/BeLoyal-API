package com.shabanaj.beloyal.features.coupon.controller;

import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.dto.CouponUpdateRequest;
import com.shabanaj.beloyal.features.coupon.service.CouponUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/coupons")
@RequiredArgsConstructor
public class CouponUpdateController {

    private final CouponUpdateService couponUpdateService;

    @PatchMapping("/{couponId}")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CouponDetailResponse> update(
            @PathVariable Long businessId,
            @PathVariable Long couponId,
            @RequestBody @Valid CouponUpdateRequest request) {
        return ResponseEntity.ok(couponUpdateService.update(businessId, couponId, request));
    }
}
