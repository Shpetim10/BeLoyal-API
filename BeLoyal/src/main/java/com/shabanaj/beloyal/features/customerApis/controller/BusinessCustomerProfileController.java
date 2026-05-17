package com.shabanaj.beloyal.features.customerApis.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.customerApis.service.CustomerProfileCreationService;
import com.shabanaj.beloyal.features.loyaltyCard.service.LoyaltyCardService;
import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.features.userProfiles.customer.dto.CustomerProfileCreationResponse;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/customers")
@RequiredArgsConstructor
public class BusinessCustomerProfileController {

    private final CustomerProfileCreationService customerProfileCreationService;
    private final LoyaltyCardService loyaltyCardService;

    @PostMapping("/profile")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<CustomerProfileCreationResponse> createCustomerProfile(
            @PathVariable Long businessId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CustomerProfileRegisterDto dto) {
        CustomerProfile profile = customerProfileCreationService.createProfileForUser(principal.getId(), dto);
        LoyaltyCard loyaltyCard = loyaltyCardService.createLoyaltyCard(profile);

        CustomerProfileCreationResponse response = new CustomerProfileCreationResponse();
        response.setFirstName(profile.getUser().getFirstName());
        response.setLastName(profile.getUser().getLastName());
        response.setQrToken(loyaltyCard.getQrToken());
        response.setManualCode(loyaltyCard.getManualCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
