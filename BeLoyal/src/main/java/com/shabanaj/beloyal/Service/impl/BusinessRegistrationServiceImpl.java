package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.Registration.businessRegistration.SubmitBusinessApplicationRequest;
import com.shabanaj.beloyal.Dto.Registration.businessRegistration.SubmitBusinessApplicationResponse;
import com.shabanaj.beloyal.Dto.Registration.customerRegistraton.RegisterUserDto;
import com.shabanaj.beloyal.Entity.Business;
import com.shabanaj.beloyal.Entity.BusinessMember;
import com.shabanaj.beloyal.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Enums.OwnerMode;
import com.shabanaj.beloyal.Enums.Role;
import com.shabanaj.beloyal.Exception.RoleNotAllowedException;
import com.shabanaj.beloyal.Exception.TCNotAcceptedException;
import com.shabanaj.beloyal.Exception.UserNotFound;
import com.shabanaj.beloyal.Security.OwnershipTokenService;
import com.shabanaj.beloyal.Service.*;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BusinessRegistrationServiceImpl implements BusinessRegistrationService {
    private final BusinessService businessService;
    private final UserService userService;
    private final BusinessMemberService businessMemberService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final OwnershipTokenService ownershipTokenService;

    public BusinessRegistrationServiceImpl(BusinessService businessService, UserService userService, BusinessMemberService businessMemberService, EmailService emailService, PasswordEncoder passwordEncoder, EmailVerificationTokenService emailVerificationTokenService, OwnershipTokenService ownershipTokenService) {
        this.businessService = businessService;
        this.userService = userService;
        this.businessMemberService = businessMemberService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationTokenService = emailVerificationTokenService;
        this.ownershipTokenService = ownershipTokenService;
    }

    @Override
    @Transactional
    public SubmitBusinessApplicationResponse registerBusiness(SubmitBusinessApplicationRequest submitBusinessApplicationRequest) {
        if (submitBusinessApplicationRequest == null){
            throw new InvalidParameterException("The data for business registration is null");
        }

        if (submitBusinessApplicationRequest.getBusinessRegistrationDto() == null){
            throw new InvalidParameterException("The data for business registration is null");
        }

        if (submitBusinessApplicationRequest.getOwnershipToken() == null && submitBusinessApplicationRequest.getUserDto() == null){
            throw new InvalidParameterException("The data for business user is null");
        }

        User businessAdmin;

        if( submitBusinessApplicationRequest.getOwnerMode().equals(OwnerMode.NEW_ACCOUNT)){
            RegisterUserDto dto= submitBusinessApplicationRequest.getUserDto();

            if(!dto.isAcceptedTc())
                throw new TCNotAcceptedException();

            //Register user first
            User user= new User();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

            user.setAcceptedTcVersion(dto.getAcceptedTcVersion());
            user.setAcceptedTcAt(LocalDateTime.now());

            businessAdmin = userService.createUser(user);

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
        } else {
            throw new InvalidParameterException("The data for user registration is null");
        }

        Business business=businessService.createBusiness(submitBusinessApplicationRequest.getBusinessRegistrationDto());

        businessMemberService.createBusinessMember(businessAdmin, business, Role.BUSINESS_ADMIN);

        return new SubmitBusinessApplicationResponse(
                business.getId(),
                business.getBusinessStatus(),
                "Registration was successful!"
        );
    }
}
