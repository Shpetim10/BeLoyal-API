package com.shabanaj.beloyal.features.registerLoyaltyPoints.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsTransactionRequest;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsTransactionResponse;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.EarnPointsTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/transactions")
@RequiredArgsConstructor
public class EarnPointsTransactionController {
    private final EarnPointsTransactionService earnPointsTransactionService;

    @PostMapping("/earn")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<EarnPointsTransactionResponse> earn(
            @PathVariable("businessId") Long businessId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody EarnPointsTransactionRequest request
        ) {

        return ResponseEntity.ok(
          earnPointsTransactionService.earnPoints(
                  businessId,
                  userPrincipal.getId(),
                  idempotencyKey,
                  request
          )
        );
    }
}
