package com.shabanaj.beloyal.features.coupon.controller;

import com.shabanaj.beloyal.features.coupon.dto.CouponDetailResponse;
import com.shabanaj.beloyal.features.coupon.dto.CouponSummaryResponse;
import com.shabanaj.beloyal.features.coupon.service.CouponViewService;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/coupons")
@RequiredArgsConstructor
public class CouponViewController {

    private final CouponViewService couponViewService;

    @GetMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN','STAFF')")
    public ResponseEntity<Page<CouponSummaryResponse>> listActive(
            @PathVariable Long businessId,
            @RequestParam(required = false) CouponStatus status,
            @RequestParam(required = false) CouponType type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        return ResponseEntity.ok(
                couponViewService.listCoupons(businessId, status, type, search, page, limit, sortBy, sortDirection));
    }

    @GetMapping("/archived")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN','STAFF')")
    public ResponseEntity<Page<CouponSummaryResponse>> listArchived(
            @PathVariable Long businessId,
            @RequestParam(required = false) CouponType type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "archivedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        return ResponseEntity.ok(
                couponViewService.listArchivedCoupons(businessId, type, search, page, limit, sortBy, sortDirection));
    }

    @GetMapping("/expired")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN','STAFF')")
    public ResponseEntity<Page<CouponSummaryResponse>> listExpired(
            @PathVariable Long businessId,
            @RequestParam(required = false) CouponType type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "endDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        return ResponseEntity.ok(
                couponViewService.listExpiredCoupons(businessId, type, search, page, limit, sortBy, sortDirection));
    }

    @GetMapping("/trash")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Page<CouponSummaryResponse>> listTrashed(
            @PathVariable Long businessId,
            @RequestParam(required = false) CouponType type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "deletedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        return ResponseEntity.ok(
                couponViewService.listTrashedCoupons(businessId, type, search, page, limit, sortBy, sortDirection));
    }

    @GetMapping("/{couponId}")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN','STAFF')")
    public ResponseEntity<CouponDetailResponse> get(
            @PathVariable Long businessId,
            @PathVariable Long couponId) {
        return ResponseEntity.ok(couponViewService.getCoupon(businessId, couponId));
    }
}
