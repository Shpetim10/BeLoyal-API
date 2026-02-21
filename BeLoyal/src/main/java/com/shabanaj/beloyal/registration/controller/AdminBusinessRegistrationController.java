package com.shabanaj.beloyal.registration.controller;

import com.shabanaj.beloyal.Security.UserPrincipal;
import com.shabanaj.beloyal.auth.service.AdminBusinessRejectionService;
import com.shabanaj.beloyal.business.service.BusinessService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.registration.service.AdminBusinessApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/besahub/admin")
@RequiredArgsConstructor
public class AdminBusinessRegistrationController {
    private final AdminBusinessRejectionService adminBusinessRejectionService;
    private final AdminBusinessApprovalService adminBusinessApprovalService;
    private final BusinessService businessService;

    private static final String HEADER_KEY = "message";

    @PostMapping("/business-applications/{businessId}/approve")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> approveBusiness(@PathVariable("businessId") Long businessId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            if (userPrincipal == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(HEADER_KEY, "Access denied"));
            }
            adminBusinessApprovalService.approveBusinessRegistration(businessId, userPrincipal.getId());
            return ResponseEntity.ok().body(Map.of(HEADER_KEY, "Business was approved"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(HEADER_KEY, e.getMessage()));
        }
    }

    @PostMapping("/business-applications/{businessId}/reject")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> rejectBusiness(@PathVariable("businessId") Long businessId,
            @RequestBody Map<String, String> body, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            if (userPrincipal == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(HEADER_KEY, "Access denied"));
            }

            String rejectionReason = body.get("rejectionReason");
            adminBusinessRejectionService.rejectBusinessRegistration(businessId, userPrincipal.getId(),
                    rejectionReason);

            return ResponseEntity.ok().body(Map.of(HEADER_KEY, "Business was rejected"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(HEADER_KEY, ex.getMessage()));
        }
    }

    @GetMapping("/business-applications")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Business>> getAllBusinessApplications() {
        List<Business> applications = businessService.getPendingBusinesses();
        return ResponseEntity.ok(applications);
    }
}
