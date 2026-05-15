package com.shabanaj.beloyal.features.dashboard.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.dashboard.dto.StaffSummaryDto;
import com.shabanaj.beloyal.features.dashboard.service.StaffSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/business/{businessId}")
@RequiredArgsConstructor
public class StaffSummaryController {

    private final StaffSummaryService staffSummaryService;

    @GetMapping("/staff-summary")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'STAFF')")
    public ResponseEntity<StaffSummaryDto> getStaffSummary(
            @PathVariable Long businessId,
            Authentication authentication) {
        Long staffUserId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(staffSummaryService.getSummary(businessId, staffUserId));
    }
}
