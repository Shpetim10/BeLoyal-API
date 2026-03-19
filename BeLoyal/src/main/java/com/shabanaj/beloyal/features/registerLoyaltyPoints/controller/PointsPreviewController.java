package com.shabanaj.beloyal.features.registerLoyaltyPoints.controller;

import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.PointsPreviewRequest;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.PointsPreviewResponse;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.PointsPreviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/transactions/")
@RequiredArgsConstructor
public class PointsPreviewController {
    private final PointsPreviewService pointsPreviewService;

    @PostMapping("/earn-points/preview")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<PointsPreviewResponse> preview(@PathVariable("businessId") Long  businessId, @Valid @RequestBody PointsPreviewRequest request){
        return ResponseEntity.ok(
                pointsPreviewService.preview(businessId, request)
        );
    }

}
