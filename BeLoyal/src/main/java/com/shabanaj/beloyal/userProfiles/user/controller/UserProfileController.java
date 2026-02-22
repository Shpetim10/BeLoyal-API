package com.shabanaj.beloyal.userProfiles.user.controller;

import com.shabanaj.beloyal.Security.UserPrincipal;
import com.shabanaj.beloyal.userProfiles.user.dto.UserDetailsDto;
import com.shabanaj.beloyal.userProfiles.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/user-profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @PreAuthorize("isAuthorized")
    public ResponseEntity<UserDetailsDto> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userPrincipal.getId()));
    }
}
