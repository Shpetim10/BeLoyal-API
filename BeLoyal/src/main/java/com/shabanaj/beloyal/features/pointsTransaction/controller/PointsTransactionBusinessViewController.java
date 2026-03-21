package com.shabanaj.beloyal.features.pointsTransaction.controller;

import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionBusinessListViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionViewDto;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionBusinessViewService;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/transactions")
@RequiredArgsConstructor
public class PointsTransactionBusinessViewController {
    private final PointsTransactionBusinessViewService pointsTransactionBusinessViewService;
    private final PointsTransactionService pointsTransactionService;

    @GetMapping
    @PreAuthorize("@businessSecurity.hasAccess(#businessId , authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<List<PointTransactionBusinessListViewDto>> getPointsTransactionList(@PathVariable("businessId") Long businessId){
        return ResponseEntity.ok(
                pointsTransactionBusinessViewService.getPointsTransactionList(businessId)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<PointTransactionViewDto> getPointTransactionView(@PathVariable("businessId") Long businessId, @PathVariable("id") Long id){
        return ResponseEntity.ok(
                pointsTransactionService.getById(id)
        );
    }
}
