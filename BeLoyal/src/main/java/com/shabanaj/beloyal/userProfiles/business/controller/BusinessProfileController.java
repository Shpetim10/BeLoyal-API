package com.shabanaj.beloyal.userProfiles.business.controller;

import com.shabanaj.beloyal.userProfiles.business.dto.BusinessProfileDto;
import com.shabanaj.beloyal.userProfiles.business.dto.BusinessProfileUpdateDto;
import com.shabanaj.beloyal.userProfiles.business.service.BusinessProfileService;
import com.shabanaj.beloyal.userProfiles.business.service.BusinessProfileUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/business")
@RequiredArgsConstructor
public class BusinessProfileController {
    private final BusinessProfileService businessProfileService;
    private final BusinessProfileUpdateService businessProfileUpdateService;

    @GetMapping("/{businessId}")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<BusinessProfileDto> getBusinessProfile(@PathVariable("businessId") Long businessId){
        return ResponseEntity.ok(businessProfileService.getBusinessProfile(businessId));
    }

    @PatchMapping("/{businessId}")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> updateBusinessProfile(@PathVariable("businessId") Long businessId,  @RequestBody @Valid BusinessProfileUpdateDto businessProfileUpdateDto){
        businessProfileUpdateService.updateBusinessProfile(businessProfileUpdateDto, businessId);

        return  ResponseEntity.ok(Map.of("message", "Business updated successfully"));
    }
}
