package com.shabanaj.beloyal.features.registration.controller;

import com.shabanaj.beloyal.features.registration.dto.businessMemberRegistration.BusinessMemberInviteDto;
import com.shabanaj.beloyal.features.registration.service.BusinessMemberInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/business")
@RequiredArgsConstructor
public class BusinessMemberInvitationController {
    private final BusinessMemberInvitationService  businessMemberInvitationService;

    @PostMapping("/{businessId}/staff/invite")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> inviteBusinessMember(@PathVariable Long businessId, @RequestBody @Valid BusinessMemberInviteDto dto) {
        businessMemberInvitationService.invite(dto, businessId);

        return ResponseEntity.ok(Map.of("message", "Invitation was sent successfully"));
    }

}
