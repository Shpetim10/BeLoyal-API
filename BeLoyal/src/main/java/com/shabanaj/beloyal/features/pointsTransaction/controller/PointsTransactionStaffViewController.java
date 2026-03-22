package com.shabanaj.beloyal.features.pointsTransaction.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionStaffListView;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionStaffViewForBusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/transactions/business-member")
@RequiredArgsConstructor
public class PointsTransactionStaffViewController {
    private final PointsTransactionStaffViewForBusinessService pointsTransactionStaffViewForBusinessService;

    @GetMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication , 'STAFF')")
    public ResponseEntity<List<PointTransactionStaffListView>> getTransactionList(
            @PathVariable("businessId") Long businessId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(
                pointsTransactionStaffViewForBusinessService.getTransactionList(userPrincipal.getId(), businessId)
        );
    }
}
