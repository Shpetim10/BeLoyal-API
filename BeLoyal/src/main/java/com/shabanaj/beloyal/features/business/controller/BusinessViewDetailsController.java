package com.shabanaj.beloyal.features.business.controller;

import com.shabanaj.beloyal.features.business.dto.BusinessDetailsDto;
import com.shabanaj.beloyal.features.business.service.BusinessViewDetailsService;
import com.shabanaj.beloyal.features.businessMember.dto.BusinessListView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/admin/businesses")
@RequiredArgsConstructor
public class BusinessViewDetailsController {
    private final BusinessViewDetailsService businessViewDetailsService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<BusinessListView>> getAllBusinesses(){
        return ResponseEntity.ok(
                businessViewDetailsService.getBusinesses()
        );
    }

    @GetMapping("/{businessId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BusinessDetailsDto> getBusinessDetails(@PathVariable("businessId") Long businessId){
        return ResponseEntity.ok(
                businessViewDetailsService.getBusinessDetails(businessId)
        );
    }
}
