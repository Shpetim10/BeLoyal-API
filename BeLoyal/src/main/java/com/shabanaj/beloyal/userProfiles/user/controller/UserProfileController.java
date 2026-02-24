package com.shabanaj.beloyal.userProfiles.user.controller;

import com.shabanaj.beloyal.Security.UserPrincipal;
import com.shabanaj.beloyal.userProfiles.user.dto.UpdateUserProfileDto;
import com.shabanaj.beloyal.userProfiles.user.dto.UserDetailsDto;
import com.shabanaj.beloyal.userProfiles.user.service.UserProfileService;
import com.shabanaj.beloyal.userProfiles.user.service.UserProfileUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/user-profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final UserProfileUpdateService userProfileUpdateService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<UserDetailsDto> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userPrincipal.getId()));
    }

    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<Map<String, String>> updateUser(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid UpdateUserProfileDto dto){
        userProfileUpdateService.updateUserProfile(dto, userPrincipal.getId());

        return ResponseEntity.ok(Map.of("message", "User updated successfully"));
    }
}
