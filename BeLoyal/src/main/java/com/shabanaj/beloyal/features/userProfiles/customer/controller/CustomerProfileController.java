package com.shabanaj.beloyal.features.userProfiles.customer.controller;

import com.shabanaj.beloyal.features.loyaltyCard.service.LoyaltyCardService;
import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.features.userProfiles.customer.dto.CustomerProfileCreationResponse;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.userProfiles.customer.dto.CustomerProfileUpdateDto;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileUpdateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/customer")
public class CustomerProfileController {
    private final CustomerProfileService customerProfileService;
    private final UserService userService;
    private final CustomerProfileUpdateService customerProfileUpdateService;
    private final LoyaltyCardService loyaltyCardService;

    public CustomerProfileController(CustomerProfileService customerProfileService, UserService userService, CustomerProfileUpdateService customerProfileUpdateService, LoyaltyCardService loyaltyCardService) {
        this.customerProfileService = customerProfileService;
        this.userService = userService;
        this.customerProfileUpdateService = customerProfileUpdateService;
        this.loyaltyCardService = loyaltyCardService;
    }

    @PostMapping("/me/create-profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerProfileCreationResponse> createCustomerProfile(@AuthenticationPrincipal UserPrincipal principal,
                                                                                 @RequestBody @Valid CustomerProfileRegisterDto dto) {
        // get user
        User user = userService.getUserOrThrow(principal.getId());

        // generate customer profile
        CustomerProfile customerProfile= customerProfileService.createCustomerPofile(user, dto);

        // generate loyalty card
        LoyaltyCard loyaltyCard= loyaltyCardService.createLoyaltyCard(customerProfile);

        // Create Response dto
        CustomerProfileCreationResponse customerProfileCreationResponse = new CustomerProfileCreationResponse();
        customerProfileCreationResponse.setFirstName(user.getFirstName());
        customerProfileCreationResponse.setLastName(user.getLastName());
        customerProfileCreationResponse.setQrToken(loyaltyCard.getQrToken());
        customerProfileCreationResponse.setManualCode(loyaltyCard.getManualCode());

        return ResponseEntity.ok(customerProfileCreationResponse);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerProfile> getCustomerProfile(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userService.getUserOrThrow(principal.getId());
        return ResponseEntity.ok(customerProfileService.getCustomerProfileByUser(user));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, String>> updateCustomerProfile(@AuthenticationPrincipal UserPrincipal principal, @RequestBody @Valid CustomerProfileUpdateDto dto) {
        customerProfileUpdateService.updateCustomerProfile(dto, principal.getId());
        return ResponseEntity.ok(Map.of("message", "Customer profile updated successfully!"));
    }
}
