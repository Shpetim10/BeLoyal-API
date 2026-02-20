package com.shabanaj.beloyal.registration.service.impl;

import com.shabanaj.beloyal.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.businessMember.service.BusinessMemberService;
import com.shabanaj.beloyal.email.service.EmailService;
import com.shabanaj.beloyal.model.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.model.Entity.StaffInviteToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.UserStatus;
import com.shabanaj.beloyal.registration.dto.businessMemberRegistration.BusinessMemberRegisterDto;
import com.shabanaj.beloyal.registration.service.BusinessMemberAcceptanceService;
import com.shabanaj.beloyal.token.repository.StaffInviteTokenRepository;
import com.shabanaj.beloyal.token.service.EmailVerificationTokenService;
import com.shabanaj.beloyal.token.service.StaffInviteTokenService;
import com.shabanaj.beloyal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BusinessMemberAcceptanceServiceImpl implements BusinessMemberAcceptanceService {
    private final StaffInviteTokenService staffInviteTokenService;
    private final StaffInviteTokenRepository staffInviteTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final BusinessMemberService businessMemberService;
    private final EmailVerificationTokenService emailVerificationTokenService;

    @Override
    @Transactional
    public void registerNewUserAsBusinessMember(BusinessMemberRegisterDto dto) {
        if(dto==null) return;

        StaffInviteToken token= staffInviteTokenService.getStaffInviteToken(dto.getToken());

        if(token.isUsed()){
            return;
        }

        User user= token.getUser();
        // update fields
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        user.setAcceptedTcVersion(dto.getAcceptedTcVersion());
        user.setAcceptedTcAt(LocalDateTime.now());

        // Send activation email
        EmailVerificationToken emailVerificationToken= emailVerificationTokenService.generateEmailVerificationToken(user);
        emailService.sendActivationEmail(user, emailVerificationToken.getToken());
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        userService.createUser(user);

        // mark token as used
        token.setUsed(true);
        staffInviteTokenRepository.save(token);

        //persist as member
        businessMemberService.createBusinessMember(user,token.getBusiness() ,token.getRole(), token.getHiredAt());
    }

    @Override
    @Transactional
    public void assignExistingUserToBusiness(String token, Long authenticatedUserId) {
        if(token==null) return;

        StaffInviteToken staffInviteToken = staffInviteTokenService.getStaffInviteToken(token);

        if(!authenticatedUserId.equals(staffInviteToken.getUser().getId())){
            throw new AuthenticationServiceException("Wrong authenticated user");
        }

        // mark token as used
        staffInviteToken.setUsed(true);
        staffInviteTokenRepository.save(staffInviteToken);

        //persist as member
        businessMemberService.createBusinessMember(
                staffInviteToken.getUser(),
                staffInviteToken.getBusiness(),
                staffInviteToken.getRole(),
                staffInviteToken.getHiredAt()
        );
    }
}
