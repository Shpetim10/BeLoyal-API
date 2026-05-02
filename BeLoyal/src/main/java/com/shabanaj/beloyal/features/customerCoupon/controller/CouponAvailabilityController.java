package com.shabanaj.beloyal.features.customerCoupon.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.customerCoupon.dto.AvailableCouponsResponse;
import com.shabanaj.beloyal.features.customerCoupon.dto.ValidateRedemptionResponse;
import com.shabanaj.beloyal.features.customerCoupon.service.CouponAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CouponAvailabilityController {

    private final CouponAvailabilityService couponAvailabilityService;

    @GetMapping("/api/besahub/customer/businesses/{businessId}/available-coupons")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AvailableCouponsResponse> getAvailableCoupons(
            @PathVariable Long businessId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(couponAvailabilityService.getAvailableCoupons(businessId, userPrincipal.getId()));
    }

    @PostMapping("/api/besahub/customer/coupons/{couponId}/validate-redemption")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ValidateRedemptionResponse> validateRedemption(
            @PathVariable Long couponId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(couponAvailabilityService.validateRedemption(couponId, userPrincipal.getId()));
    }
}
