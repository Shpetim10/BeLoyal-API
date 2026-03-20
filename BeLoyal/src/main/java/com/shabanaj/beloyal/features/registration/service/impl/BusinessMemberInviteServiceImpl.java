package com.shabanaj.beloyal.features.registration.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberService;
import com.shabanaj.beloyal.common.Helpers.UserFinder;
import com.shabanaj.beloyal.common.email.service.EmailService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.StaffInviteToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.UserStatus;
import com.shabanaj.beloyal.features.registration.dto.businessMemberRegistration.BusinessMemberInviteDto;
import com.shabanaj.beloyal.features.registration.service.BusinessMemberInvitationService;
import com.shabanaj.beloyal.features.token.service.StaffInviteTokenService;
import com.shabanaj.beloyal.features.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final BusinessMemberService businessMemberService;
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public void invite(BusinessMemberInviteDto dto, Long businessId) {
        validateFieldsOrThrow(dto, businessId);

        User user = userFinder.findByEmailOrNull(dto.getEmail());
        Business business = businessService.getBusinessById(businessId); // throws if not found
        boolean isExitingUser = user != null;

        if (!isExitingUser) {
            user = new User();
            user.setFirstName(" ");
            user.setLastName(" ");
            user.setPasswordHash("  ");
            user.setEmail(dto.getEmail());

            String rawUsername = UUID.randomUUID().toString();
            String username = String.format("%-50s", rawUsername).substring(0, 50);
            user.setUsername(username);
            user.setStatus(UserStatus.PENDING_VERIFICATION);

            userService.createUser(user);
        }

        // generate token
        StaffInviteToken staffInviteToken = staffInviteTokenService.generateStaffInviteToken(user, business,
                isExitingUser);

        // send email
        emailService.sendStaffInvitationEmail(staffInviteToken, business, dto.getRole());

        // create business member
        businessMemberService.createBusinessMember(
                user,
                business,
                dto.getRole(),
                dto.getHireDate());
    }

    private void validateFieldsOrThrow(BusinessMemberInviteDto dto, Long businessId) {
        if (dto == null || businessId == null) {
            throw new InvalidParameterException();
        }
    }
}
