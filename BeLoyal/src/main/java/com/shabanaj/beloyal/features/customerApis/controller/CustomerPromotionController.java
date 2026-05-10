package com.shabanaj.beloyal.features.customerApis.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerPromotionDto;
import com.shabanaj.beloyal.features.customerApis.service.CustomerPromotionViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/besahub/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerPromotionController {

    private final CustomerPromotionViewService customerPromotionViewService;

    @GetMapping("/promotions")
    public ResponseEntity<List<CustomerPromotionDto>> getPromotions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(customerPromotionViewService.getPromotions(principal.getId(), status));
    }
}
