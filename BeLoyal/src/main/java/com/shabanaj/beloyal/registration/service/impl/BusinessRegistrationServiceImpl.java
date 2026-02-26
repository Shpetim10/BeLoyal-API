package com.shabanaj.beloyal.registration.service.impl;

import com.shabanaj.beloyal.registration.dto.businessRegistration.SubmitBusinessApplicationRequest;
import com.shabanaj.beloyal.registration.dto.businessRegistration.SubmitBusinessApplicationResponse;
import com.shabanaj.beloyal.registration.dto.RegisterUserDto;
import com.shabanaj.beloyal.business.service.BusinessService;
import com.shabanaj.beloyal.email.service.EmailService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.OwnerMode;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.common.Exception.UserNotFound;
import com.shabanaj.beloyal.Security.OwnershipTokenService;
import com.shabanaj.beloyal.registration.service.BusinessApplicationValidatorService;
import com.shabanaj.beloyal.businessMember.service.BusinessMemberService;
import com.shabanaj.beloyal.registration.service.BusinessRegistrationService;
import com.shabanaj.beloyal.token.service.EmailVerificationTokenService;
import com.shabanaj.beloyal.user.service.UserRegistrationBuilderService;
import com.shabanaj.beloyal.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BusinessRegistrationServiceImpl implements BusinessRegistrationService {
    private final BusinessService businessService;
    private final UserService userService;
    private final BusinessMemberService businessMemberService;
    private final EmailService emailService;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final OwnershipTokenService ownershipTokenService;
    private final UserRegistrationBuilderService userRegistrationBuilderService;
    private final BusinessApplicationValidatorService businessApplicationValidatorService;
    private final Clock clock;

    @Override
    @Transactional
    public SubmitBusinessApplicationResponse registerBusiness(SubmitBusinessApplicationRequest submitBusinessApplicationRequest) {
        businessApplicationValidatorService.validateBusinessApplicationOrThrow(submitBusinessApplicationRequest);

        User businessAdmin;

        if( submitBusinessApplicationRequest.getOwnerMode().equals(OwnerMode.NEW_ACCOUNT)){
            RegisterUserDto dto= submitBusinessApplicationRequest.getUserDto();

            businessAdmin = userService.createUser(
                    userRegistrationBuilderService.buildUserFromRegistration(dto)
            );

            //Create verification token and send it to user's email
            EmailVerificationToken verificationToken =emailVerificationTokenService.generateEmailVerificationToken(businessAdmin);

            emailService.sendActivationEmail(businessAdmin, verificationToken.getToken());
        }
        else if( submitBusinessApplicationRequest.getOwnerMode().equals(OwnerMode.EXISTING_AUTHENTICATED)){
            if (submitBusinessApplicationRequest.getOwnershipToken() == null || submitBusinessApplicationRequest.getOwnershipToken().isBlank()) {
                throw new InvalidParameterException("ownershipToken is required for existing users");
            }

            Claims claims = ownershipTokenService.verify(submitBusinessApplicationRequest.getOwnershipToken());
            Long userId = ((Number) claims.get("userId")).longValue();

            businessAdmin = userService.getUserById(userId)
                    .orElseThrow(UserNotFound::new);
        }
        else {
            throw new InvalidParameterException("The data for user registration is null");
        }

        Business business=businessService.createBusiness(submitBusinessApplicationRequest.getBusinessRegistrationDto());

        businessMemberService.createBusinessMember(businessAdmin, business, Role.BUSINESS_ADMIN, LocalDate.now(clock));

        emailService.sendBusinessRegistrationEmail(businessAdmin, business);

        return new SubmitBusinessApplicationResponse(
                business.getId(),
                business.getBusinessStatus(),
                "Registration was successful!"
        );
    }
}
