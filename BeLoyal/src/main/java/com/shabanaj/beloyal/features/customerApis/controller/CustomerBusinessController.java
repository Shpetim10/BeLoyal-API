package com.shabanaj.beloyal.features.customerApis.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerBusinessDto;
import com.shabanaj.beloyal.features.customerApis.service.BusinessViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerBusinessController {

    private final BusinessViewService businessViewService;

    @GetMapping("/businesses")
    public ResponseEntity<List<CustomerBusinessDto>> getBusinesses(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(businessViewService.getBusinessesForCustomer(principal.getId()));
    }
}
