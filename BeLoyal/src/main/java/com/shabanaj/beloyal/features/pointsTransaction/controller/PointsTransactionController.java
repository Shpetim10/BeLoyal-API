package com.shabanaj.beloyal.features.pointsTransaction.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/transactions/{id}")
@RequiredArgsConstructor
public class PointsTransactionController {
    private final PointsTransactionService pointsTransactionService;

    @GetMapping
    @PreAuthorize("@transactionSecurity.canViewTransaction(#id, authentication)")
    public ResponseEntity<PointTransactionViewDto> getPointTransactionView(@PathVariable("id") Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(
                pointsTransactionService.getById(id)
        );
    }
}
