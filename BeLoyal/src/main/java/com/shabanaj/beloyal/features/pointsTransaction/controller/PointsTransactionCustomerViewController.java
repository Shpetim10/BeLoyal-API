package com.shabanaj.beloyal.features.pointsTransaction.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionCustomerAllListViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionCustomerBusinessListViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionCustomerViewService;
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
@RequestMapping("/api/besahub/customer/points-transactions")
@RequiredArgsConstructor
public class PointsTransactionCustomerViewController {
    private final PointsTransactionCustomerViewService pointsTransactionCustomerViewService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<PointTransactionCustomerAllListViewDto>> getPointTransactionCustomerListViewDto(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(pointsTransactionCustomerViewService.getPointTransactionCustomerListViewDto(userPrincipal.getId()));
    }

    @GetMapping("/business/{businessId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<PointTransactionCustomerBusinessListViewDto>> getPointTransactionCustomerBusinessListViewDto(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("businessId") Long businessId) {
        return ResponseEntity.ok(pointsTransactionCustomerViewService.getPointTransactionCustomerBusinessListVIewDto(userPrincipal.getId(), businessId));
    }
}
