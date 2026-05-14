package com.shabanaj.beloyal.features.customerCoupon.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.customerCoupon.dto.StaffCouponScanRequest;
import com.shabanaj.beloyal.features.customerCoupon.dto.StaffCouponScanResponse;
import com.shabanaj.beloyal.features.customerCoupon.service.StaffCouponRedemptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/coupons")
@RequiredArgsConstructor
public class StaffCouponRedemptionController {

    private final StaffCouponRedemptionService staffCouponRedemptionService;

    @PostMapping("/scan")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<StaffCouponScanResponse> scan(
            @PathVariable Long businessId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody StaffCouponScanRequest request) {
        return ResponseEntity.ok(
                staffCouponRedemptionService.scanAndRedeem(businessId, userPrincipal.getId(), request)
        );
    }
}
