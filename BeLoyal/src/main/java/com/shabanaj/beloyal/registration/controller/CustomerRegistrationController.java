package com.shabanaj.beloyal.registration.controller;

import com.shabanaj.beloyal.registration.dto.customerRegistraton.RegisterUserDto;
import com.shabanaj.beloyal.registration.service.CustomerRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/auth")
@RequiredArgsConstructor
public class CustomerRegistrationController {
    private final CustomerRegistrationService customerRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterUserDto dto) {
        try{
            customerRegistrationService.createCustomer(dto);

            return  ResponseEntity.ok("User successfully registered!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
