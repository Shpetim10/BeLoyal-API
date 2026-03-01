package com.shabanaj.beloyal.features.registration.service.impl;

import com.shabanaj.beloyal.common.Exception.RoleNotAllowedException;
import com.shabanaj.beloyal.common.email.service.EmailService;
import com.shabanaj.beloyal.model.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.features.registration.dto.RegisterUserDto;
import com.shabanaj.beloyal.features.registration.service.CustomerRegistrationService;
import com.shabanaj.beloyal.common.token.service.EmailVerificationTokenService;
import com.shabanaj.beloyal.features.user.service.UserRegistrationBuilderService;
import com.shabanaj.beloyal.features.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationServiceImpl implements CustomerRegistrationService {
    private final UserService userService;
    private final EmailService emailService;
    private final UserRegistrationBuilderService userRegistrationBuilderService;
    private final EmailVerificationTokenService emailVerificationTokenService;

    @Override
    @Transactional
    public void createCustomer(RegisterUserDto dto) {
        User user= userRegistrationBuilderService.buildUserFromRegistration(dto);

        if(user.getRoles().isEmpty())
            user.getRoles().add(Role.CUSTOMER);
        else{
            throw new RoleNotAllowedException();
        }

        user= userService.createUser(user);

        //Create verification token and send it to user's email
        EmailVerificationToken verificationToken =emailVerificationTokenService.generateEmailVerificationToken(user);

        emailService.sendActivationEmail(user, verificationToken.getToken());
    }
}
