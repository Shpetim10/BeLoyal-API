package com.shabanaj.beloyal.features.customerCoupon.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.customerCoupon.dto.CouponRedeemResponse;
import com.shabanaj.beloyal.features.customerCoupon.dto.CustomerCouponDetailResponse;
import com.shabanaj.beloyal.features.customerCoupon.service.CouponRedemptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/besahub/customer")
@RequiredArgsConstructor
public class CouponRedemptionController {

    private final CouponRedemptionService couponRedemptionService;

    @PostMapping("/coupons/{couponId}/redeem")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CouponRedeemResponse> redeem(
            @PathVariable Long couponId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponRedemptionService.redeem(couponId, userPrincipal.getId()));
    }

    @GetMapping("/my-coupons")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<CustomerCouponDetailResponse>> getMyCoupons(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(couponRedemptionService.getCustomerCoupons(userPrincipal.getId()));
    }

    @PostMapping("/customer-coupons/{customerCouponId}/apply")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerCouponDetailResponse> apply(
            @PathVariable Long customerCouponId,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String orderId = body != null ? body.get("orderId") : null;
        return ResponseEntity.ok(couponRedemptionService.applyCoupon(customerCouponId, userPrincipal.getId(), orderId));
    }

    @PostMapping("/customer-coupons/{customerCouponId}/use")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerCouponDetailResponse> use(
            @PathVariable Long customerCouponId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(couponRedemptionService.useCoupon(customerCouponId, userPrincipal.getId()));
    }
}
