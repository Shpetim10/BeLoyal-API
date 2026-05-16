package com.shabanaj.beloyal.features.customerApis.controller;

import com.shabanaj.beloyal.features.customerApis.service.CustomerAccountDeletionService;
import com.shabanaj.beloyal.features.customerApis.service.CustomerProfileCreationService;
import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/besahub/customer")
@RequiredArgsConstructor
public class CustomerAccountController {

    private final CustomerProfileCreationService customerProfileCreationService;
    private final CustomerAccountDeletionService customerAccountDeletionService;
    private final UserRepository userRepository;

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CustomerProfile> createProfile(@RequestBody CustomerProfileRegisterDto dto) {
        CustomerProfile profile = customerProfileCreationService.createProfileForCurrentUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @DeleteMapping("/account")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> deleteAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        customerAccountDeletionService.deleteCustomerAccount(userId);
        return ResponseEntity.noContent().build();
    }
}
