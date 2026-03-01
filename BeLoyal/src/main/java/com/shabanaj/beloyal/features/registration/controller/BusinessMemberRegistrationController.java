package com.shabanaj.beloyal.features.registration.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.registration.dto.businessMemberRegistration.AcceptInviteDto;
import com.shabanaj.beloyal.features.registration.dto.businessMemberRegistration.BusinessMemberRegisterDto;
import com.shabanaj.beloyal.features.registration.service.BusinessMemberAcceptanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/auth")
@RequiredArgsConstructor
public class BusinessMemberRegistrationController {
    private final BusinessMemberAcceptanceService businessMemberAcceptanceService;

    @PostMapping("/staff/invitations/register")
    public ResponseEntity<Map<String, String>> registerBusinessMember(@RequestBody @Valid BusinessMemberRegisterDto businessMemberRegisterDto){
        businessMemberAcceptanceService.registerNewUserAsBusinessMember(businessMemberRegisterDto);

        return ResponseEntity.ok(Map.of("message", "Invitation accepted. Account created."));
    }

    @PostMapping("/staff/invitations/accept")
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<Map<String, String>> acceptInvitation(@AuthenticationPrincipal UserPrincipal authenticatedUser, @Valid @RequestBody AcceptInviteDto dto){
        businessMemberAcceptanceService.assignExistingUserToBusiness(dto.getToken(), authenticatedUser.getId());

        return ResponseEntity.ok(Map.of("message", "Invitation accepted. Account created."));
    }
}
