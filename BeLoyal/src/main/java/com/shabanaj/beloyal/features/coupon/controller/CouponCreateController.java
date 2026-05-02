package com.shabanaj.beloyal.features.coupon.controller;

import com.shabanaj.beloyal.features.coupon.dto.CouponCreateRequest;
import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.service.CouponCreateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/coupons")
@RequiredArgsConstructor
public class CouponCreateController {

    private final CouponCreateService couponCreateService;

    @PostMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<CouponDetailResponse> create(
            @PathVariable Long businessId,
            @RequestBody @Valid CouponCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponCreateService.create(businessId, request));
    }
}
