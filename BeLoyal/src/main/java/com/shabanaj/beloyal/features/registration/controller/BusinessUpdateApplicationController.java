package com.shabanaj.beloyal.features.registration.controller;

import com.shabanaj.beloyal.features.registration.dto.businessRegistration.BusinessRegistrationDto;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.SubmitBusinessApplicationResponse;
import com.shabanaj.beloyal.features.registration.service.BusinessUpdateRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/business/{businessId}")
@RequiredArgsConstructor
public class BusinessUpdateApplicationController {
    private final BusinessUpdateRegistrationService businessUpdateRegistrationService;

    @PatchMapping("/application")
    @PreAuthorize("@businessSecurity.hasAccessToCheckStatus(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, SubmitBusinessApplicationResponse>> update(@PathVariable("businessId") Long businessId, @Valid @RequestBody BusinessRegistrationDto dto){
        return ResponseEntity.ok(
                Map.of("message", businessUpdateRegistrationService.update(
                        businessId, dto
                ))
        );
    }

    @GetMapping("/application")
    @PreAuthorize("@businessSecurity.hasAccessToCheckStatus(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<BusinessRegistrationDto> getApplication(@PathVariable("businessId") Long businessId){
        return ResponseEntity.ok(businessUpdateRegistrationService.get(businessId));
    }

}
