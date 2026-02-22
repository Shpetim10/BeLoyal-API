package com.shabanaj.beloyal.userProfiles.staff.controller;

import com.shabanaj.beloyal.Security.UserPrincipal;
import com.shabanaj.beloyal.userProfiles.staff.dto.BusinessMemberDetailsDto;
import com.shabanaj.beloyal.userProfiles.staff.service.BusinessMemberProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/besahub/business-member")
@RequiredArgsConstructor
public class BusinessMemberProfileController {
    private final BusinessMemberProfileService businessMemberProfileService;

    @GetMapping("/me/{businessId}")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'STAFF', 'BUSINESS_ADMIN')")
    public ResponseEntity<BusinessMemberDetailsDto> getBusinessMemberdetails(@PathVariable Long businessId, @AuthenticationPrincipal UserPrincipal userPrincipal){
        return ResponseEntity.ok(businessMemberProfileService.getStaffProfileDetails(userPrincipal.getId(), businessId));
    }
}
