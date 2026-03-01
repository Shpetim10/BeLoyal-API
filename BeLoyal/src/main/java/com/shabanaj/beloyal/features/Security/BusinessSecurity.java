package com.shabanaj.beloyal.features.Security;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import com.shabanaj.beloyal.model.Enums.UserStatus;
import com.shabanaj.beloyal.features.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("businessSecurity")
@RequiredArgsConstructor
public class BusinessSecurity {
    private final BusinessMemberRepository businessMemberRepository;
    private final UserService userService;
    private final BusinessService businessService;

    public boolean hasAccessToCheckStatus(Long businessId, Authentication auth, String... roles) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getId();

        return businessMemberRepository.existsByBusinessIdAndUserIdAndRoleIn(
                businessId, userId, List.of(roles)
        );
    }

    public boolean hasAccess(Long businessId, Authentication auth, String... roles) {
        try{
            Long userId = ((UserPrincipal) auth.getPrincipal()).getId();

            boolean exists = businessMemberRepository.existsByBusinessIdAndUserIdAndRoleIn(
                    businessId, userId, List.of(roles)
            );

            if(!exists){
                return false;
            }

            User user= userService.getUserOrThrow(userId);
            Business business= businessService.getBusinessById(businessId);

            return user.getStatus()== UserStatus.ENABLED && business.getBusinessStatus()== BusinessStatus.ACTIVE;
        } catch (Exception e) {
            return false;
        }
    }
}

