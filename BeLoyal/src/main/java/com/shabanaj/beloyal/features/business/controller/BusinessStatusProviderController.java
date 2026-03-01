package com.shabanaj.beloyal.features.business.controller;

import com.shabanaj.beloyal.features.business.service.BusinessStatusProviderService;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/staff")
@RequiredArgsConstructor
public class BusinessStatusProviderController {
    private final BusinessStatusProviderService businessStatusProviderService;

    private static final String HEADER_KEY="message";

    @GetMapping("/{businessId}/status")
    @PreAuthorize("@businessSecurity.hasAccessToCheckStatus(#businessId, authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<Map<String, ?>> getBusinessStatus(@PathVariable("businessId") Long businessId){
        try{
            BusinessStatus status= businessStatusProviderService.getBusinessStatus(businessId);
            return ResponseEntity.ok(Map.of("status",status));
        } catch(Exception e){
            return ResponseEntity.badRequest().body(Map.of(HEADER_KEY,e.getMessage()));
        }
    }
}
