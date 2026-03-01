package com.shabanaj.beloyal.features.businessMember.controller;

import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberStatusServcie;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/staff/{memberId}")
@RequiredArgsConstructor
public class BusinessMemberActivationController {
    private final BusinessMemberStatusServcie businessMemberStatusService;

    @PostMapping("/activate")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> activate(@PathVariable Long businessId, @PathVariable Long memberId){
        businessMemberStatusService.changeStatusAndSave(memberId, BusinessMember.MemberStatus.ACTIVE);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/deactivate")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> deactivate(@PathVariable Long businessId, @PathVariable Long memberId){
        businessMemberStatusService.changeStatusAndSave(memberId, BusinessMember.MemberStatus.INACTIVE);

        return ResponseEntity.ok().build();
    }
}
