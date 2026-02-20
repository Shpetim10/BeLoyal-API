package com.shabanaj.beloyal.customer.controller;

import com.shabanaj.beloyal.registration.dto.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.Security.UserPrincipal;
import com.shabanaj.beloyal.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub" +
        "/customer")
public class CustomerProfileController {
    private final CustomerProfileService customerProfileService;
    private final UserService userService;

    public CustomerProfileController(CustomerProfileService customerProfileService, UserService userService) {
        this.customerProfileService = customerProfileService;
        this.userService = userService;
    }

    @PostMapping("/me/create-profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> createCustomerProfile(@AuthenticationPrincipal UserPrincipal principal,
                                                        @RequestBody @Valid CustomerProfileRegisterDto dto){
        try{
            User user= userService.getUserOrThrow(principal.getId());
            customerProfileService.createCustomerPofile(user, dto);

            return ResponseEntity.ok("Customer profile created successfully!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerProfile> getCustomerProfile(@AuthenticationPrincipal UserPrincipal principal){
        try{
            User user= userService.getUserOrThrow(principal.getId());
            return ResponseEntity.ok(customerProfileService.getCustomerProfileByUser(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }
}
