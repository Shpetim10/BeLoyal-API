package com.shabanaj.beloyal.features.customerApis.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerHomeResponse;
import com.shabanaj.beloyal.features.customerApis.service.CustomerHomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerHomeController {

    private final CustomerHomeService customerHomeService;

    @GetMapping("/home")
    public ResponseEntity<CustomerHomeResponse> getHome(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(customerHomeService.getHome(principal.getId()));
    }
}
