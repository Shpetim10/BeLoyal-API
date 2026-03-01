package com.shabanaj.beloyal.features.auth.policy;

import com.shabanaj.beloyal.features.auth.dto.BusinessProfileInfo;
import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BusinessAccessPolicy {
    private final BusinessMemberRepository businessMemberRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<BusinessProfileInfo> getAccessibleProfiles(User user) {
        if (!isStaffOrAdmin(user))
            return List.of();

        List<BusinessMember> userRoles = businessMemberRepository.findByUser(user);
        List<BusinessProfileInfo> accessibleProfiles = new ArrayList<>();

        for (BusinessMember userRole : userRoles) {
            boolean memberEnabled = userRole.getMemberStatus() == BusinessMember.MemberStatus.ACTIVE;
            boolean businessActive = userRole.getBusiness().getBusinessStatus() == BusinessStatus.ACTIVE;

            BusinessProfileInfo businessProfileInfo = new BusinessProfileInfo();
            businessProfileInfo.setBusinessId(userRole.getBusiness().getId());
            businessProfileInfo.setBusinessName(userRole.getBusiness().getBusinessName());
            businessProfileInfo.setRole(userRole.getRole());
            businessProfileInfo.setActive(memberEnabled && businessActive);
            businessProfileInfo.setBusinessStatus(userRole.getBusiness().getBusinessStatus().name());
            businessProfileInfo.setRejectionReason(userRole.getBusiness().getRejectionReason());
            businessProfileInfo.setMemberStatus(userRole.getMemberStatus().name());

            accessibleProfiles.add(businessProfileInfo);
        }

        return accessibleProfiles;
    }

    private boolean isStaffOrAdmin(User user) {
        return !businessMemberRepository.findByUser(user).isEmpty();
    }
}
