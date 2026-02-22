package com.shabanaj.beloyal.userProfiles.staff.service.impl;

import com.shabanaj.beloyal.business.service.BusinessService;
import com.shabanaj.beloyal.businessMember.service.BusinessMemberService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.user.service.UserService;
import com.shabanaj.beloyal.userProfiles.staff.dto.BusinessMemberDetailsDto;
import com.shabanaj.beloyal.userProfiles.staff.service.BusinessMemberProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessMemberProfileServiceImpl implements BusinessMemberProfileService {
    private final BusinessMemberService businessMemberService;
    private final UserService userService;
    private final BusinessService businessService;

    @Override
    public BusinessMemberDetailsDto getStaffProfileDetails(Long userId, Long businessId) {
        User user = userService.getUserOrThrow(userId);
        Business business= businessService.getBusinessById(businessId);

        return new BusinessMemberDetailsDto(
                businessMemberService.getBusinessMemberByUserAndBusiness(user,business)
        );
    }
}
