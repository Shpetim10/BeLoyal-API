package com.shabanaj.beloyal.features.loyaltyAccount.controller;

import com.shabanaj.beloyal.features.customerLookup.dto.CustomerLookupDto;
import com.shabanaj.beloyal.features.loyaltyAccount.service.LoyaltyAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/customers/")
@RequiredArgsConstructor
public class LoyaltyAccountController {
    private final LoyaltyAccountService loyaltyAccountService;

    @GetMapping("/lookup")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<CustomerLookupDto> getCustomerByQrCode(@PathVariable("businessId") Long businessId, @RequestParam("qrToken") String qrToken){

        return null;
    }
}
