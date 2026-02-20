package com.shabanaj.beloyal.registration.service.impl;

import com.shabanaj.beloyal.business.service.BusinessService;
import com.shabanaj.beloyal.businessMember.service.BusinessMemberService;
import com.shabanaj.beloyal.common.Helpers.UserFinder;
import com.shabanaj.beloyal.email.service.EmailService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.StaffInviteToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.UserStatus;
import com.shabanaj.beloyal.registration.dto.businessMemberRegistration.BusinessMemberInviteDto;
import com.shabanaj.beloyal.registration.service.BusinessMemberInvitationService;
import com.shabanaj.beloyal.token.service.StaffInviteTokenService;
import com.shabanaj.beloyal.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessMemberInviteServiceImpl implements BusinessMemberInvitationService {
    private final BusinessService businessService;
    private final UserFinder userFinder;
    private final EmailService emailService;
    private final UserService userService;
    private final StaffInviteTokenService staffInviteTokenService;

    @Override
    @Transactional
    public void invite(BusinessMemberInviteDto dto, Long businessId) {
        validateFieldsOrThrow(dto,  businessId);

        User user = userFinder.findByEmailOrNull(dto.getEmail());
        Business business= businessService.getBusinessById(businessId); //throws if not found
        boolean isExitingUser=user!=null;

        if(!isExitingUser){
            user=new User();
            user.setEmail(dto.getEmail());
            user.setUsername(UUID.randomUUID().toString());
            user.setStatus(UserStatus.INVITED);
            userService.createUser(user);
        }

        //generate token
        StaffInviteToken staffInviteToken = staffInviteTokenService.generateStaffInviteToken(user, business, dto.getHireDate(), isExitingUser);

        // send email
        emailService.sendStaffInvitationEmail(staffInviteToken,business, dto.getRole());
    }

    private void validateFieldsOrThrow(BusinessMemberInviteDto dto, Long businessId) {
        if(dto == null || businessId == null){
            throw new InvalidParameterException();
        }
    }
}
