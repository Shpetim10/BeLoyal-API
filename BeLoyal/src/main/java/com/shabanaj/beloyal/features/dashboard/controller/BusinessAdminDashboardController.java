package com.shabanaj.beloyal.features.dashboard.controller;

import com.shabanaj.beloyal.features.dashboard.dto.BusinessAdminDashboardDto;
import com.shabanaj.beloyal.features.dashboard.service.BusinessAdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/business/{businessId}")
@RequiredArgsConstructor
public class BusinessAdminDashboardController {

    private final BusinessAdminDashboardService businessAdminDashboardService;

    @GetMapping("/dashboard-summary")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<BusinessAdminDashboardDto> getDashboardSummary(@PathVariable Long businessId) {
        return ResponseEntity.ok(businessAdminDashboardService.getSummary(businessId));
    }
}
