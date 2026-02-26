package com.shabanaj.beloyal.registration.controller;

import com.shabanaj.beloyal.registration.dto.RegisterUserDto;
import com.shabanaj.beloyal.registration.service.CustomerRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/auth")
@RequiredArgsConstructor
public class CustomerRegistrationController {
    private final CustomerRegistrationService customerRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody @Valid RegisterUserDto dto) {
        customerRegistrationService.createCustomer(dto);

        return  ResponseEntity.ok(Map.of("message","User successfully registered!"));
    }
}
