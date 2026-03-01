package com.shabanaj.beloyal.features.businessMember.controller;

import com.shabanaj.beloyal.features.businessMember.dto.BusinessMemberDetailsDto;
import com.shabanaj.beloyal.features.businessMember.dto.BusinessMemberStatusDto;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberService;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberStatusServcie;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/besahub/business")
@RequiredArgsConstructor
public class BusinessMemberController {
    private final BusinessMemberService  businessMemberService;
    private final BusinessMemberStatusServcie businessMemberStatusServcie;

    @GetMapping("/{businessId}/staff")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<List<BusinessMemberDetailsDto>> getStaff(@PathVariable Long businessId) {
        List<BusinessMember> businessMembers= businessMemberService.getBusinessMembersByBusinessIdAndRole(businessId, Role.STAFF);
        List<BusinessMemberDetailsDto> details= businessMembers.stream().map(BusinessMemberDetailsDto::new).toList();
        return ResponseEntity.ok(details);
    }

    @PatchMapping("/{businessId}/staff/{memberId}/status")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> changeStatus(@PathVariable Long businessId, @PathVariable Long memberId, @RequestBody BusinessMemberStatusDto dto) {
        businessMemberStatusServcie.changeStatusAndSave(memberId, dto.getMemberStatus());

        return ResponseEntity.ok(Map.of("message", "Status has been changed"));
    }
}
