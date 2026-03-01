package com.shabanaj.beloyal.features.passwordChanger.controller;

import com.shabanaj.beloyal.features.Security.UserPrincipal;
import com.shabanaj.beloyal.features.passwordChanger.dto.AuthenticatedPasswordChangeRequest;
import com.shabanaj.beloyal.features.passwordChanger.dto.AuthenticatedPasswordChangeResponse;
import com.shabanaj.beloyal.features.passwordChanger.service.AuthenticatedPasswordChangerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/user-profile")
@RequiredArgsConstructor
public class AuthenticatedPasswordChangerController {
    private final AuthenticatedPasswordChangerService authenticatedPasswordChangerService;

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<AuthenticatedPasswordChangeResponse> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid AuthenticatedPasswordChangeRequest request){
        AuthenticatedPasswordChangeResponse res= authenticatedPasswordChangerService.changePassword(userPrincipal.getId(), request);

        return ResponseEntity.ok().body(res);
    }
}
