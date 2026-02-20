package com.shabanaj.beloyal.registration.controller;

import com.shabanaj.beloyal.registration.dto.businessRegistration.SubmitBusinessApplicationRequest;
import com.shabanaj.beloyal.registration.dto.businessRegistration.SubmitBusinessApplicationResponse;
import com.shabanaj.beloyal.registration.service.BusinessRegistrationService;
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
public class BusinessRegistrationController {
    private final BusinessRegistrationService businessRegistrationService;

    @PostMapping("/register-business")
    public ResponseEntity<?> registerBusiness(@RequestBody @Valid SubmitBusinessApplicationRequest req) {
        try{
            SubmitBusinessApplicationResponse response =  businessRegistrationService.registerBusiness(req);
            return ResponseEntity.ok(response);
        } catch (Exception ex){
            return  ResponseEntity.badRequest().body(Map.of("message",ex.getMessage()));
        }
    }
}
