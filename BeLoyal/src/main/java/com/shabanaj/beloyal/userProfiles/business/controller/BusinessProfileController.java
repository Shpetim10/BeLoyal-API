package com.shabanaj.beloyal.userProfiles.business.controller;

import com.shabanaj.beloyal.userProfiles.business.dto.BusinessProfileDto;
import com.shabanaj.beloyal.userProfiles.business.service.BusinessProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/business")
@RequiredArgsConstructor
public class BusinessProfileController {
    private final BusinessProfileService businessProfileService;

    @GetMapping("/{businessId}")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<BusinessProfileDto> getBusinessProfile(@PathVariable("businessId") Long businessId){
        return ResponseEntity.ok(businessProfileService.getBusinessProfile(businessId));
    }
}
