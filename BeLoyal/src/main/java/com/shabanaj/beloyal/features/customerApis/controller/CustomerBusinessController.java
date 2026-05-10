package com.shabanaj.beloyal.features.customerApis.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerBusinessDetailResponse;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerBusinessDto;
import com.shabanaj.beloyal.features.customerApis.service.BusinessViewService;
import com.shabanaj.beloyal.features.customerApis.service.CustomerBusinessDetailService;
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
@RequestMapping("/api/besahub/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerBusinessController {

    private final BusinessViewService businessViewService;
    private final CustomerBusinessDetailService customerBusinessDetailService;

    @GetMapping("/businesses")
    public ResponseEntity<List<CustomerBusinessDto>> getBusinesses(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(businessViewService.getBusinessesForCustomer(principal.getId()));
    }

    @GetMapping("/businesses/{businessId}")
    public ResponseEntity<CustomerBusinessDetailResponse> getBusinessDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long businessId) {
        return ResponseEntity.ok(customerBusinessDetailService.getBusinessDetail(principal.getId(), businessId));
    }
}
