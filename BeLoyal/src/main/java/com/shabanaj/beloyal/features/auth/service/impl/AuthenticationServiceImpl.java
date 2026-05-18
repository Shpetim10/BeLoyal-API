package com.shabanaj.beloyal.features.auth.service.impl;

import com.shabanaj.beloyal.features.auth.policy.BusinessAccessPolicy;
import com.shabanaj.beloyal.features.auth.service.AuthenticationService;
import com.shabanaj.beloyal.features.auth.dto.LogoutRequest;
import com.shabanaj.beloyal.features.auth.dto.RefreshRequest;
import com.shabanaj.beloyal.features.auth.dto.LoginResponse;
import com.shabanaj.beloyal.common.redis.jwtToken.TokenVersionService;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.VerifyOwnershipRequest;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.VerifyOwnershipResponse;
import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.ActivationResponse;
import com.shabanaj.beloyal.common.Exception.*;
import com.shabanaj.beloyal.common.email.service.EmailService;
import com.shabanaj.beloyal.model.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.model.Entity.RefreshToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.model.Enums.UserStatus;
import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.features.token.service.EmailVerificationTokenService;
import com.shabanaj.beloyal.features.token.service.RefreshTokenService;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.features.Security.CustomUserDetailsService;
import com.shabanaj.beloyal.features.Security.JwtService;
import com.shabanaj.beloyal.features.Security.OwnershipTokenService;
import com.shabanaj.beloyal.features.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger(AuthenticationServiceImpl.class);
    private final RefreshTokenService refreshTokenService;
    private final OwnershipTokenService ownershipTokenService;
    private final BusinessAccessPolicy businessAccessPolicy;
    private final TokenVersionService tokenVersionService;

    @Transactional
    public ActivationResponse activateUser(String token) {
        EmailVerificationToken activationToken = emailVerificationTokenService.findEmailVerificationTokenByToken(token);

        if (activationToken.isUsed()) {
            throw new TokenIsNotValidException();
        }

        if (activationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Activation token has expired");
        }

        User currentUser = activationToken.getUser();
        currentUser.setStatus(UserStatus.ENABLED);
        currentUser.setEmailVerified(true);
        currentUser.setEmailVerifiedAt(LocalDateTime.now());

        emailVerificationTokenService.markTokenAsUsed(activationToken);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(currentUser.getEmail());
        // Generate access token
        int jwtVersion= tokenVersionService.getVersion(currentUser.getId());
        String jwt = jwtService.generateAccessToken(userDetails, currentUser.getId(), jwtVersion);
        // create refresh token
        String refresh = refreshTokenService.create(currentUser, null, null);

        currentUser.setLastLoginAt(LocalDateTime.now());
        userRepository.save(currentUser);

        return buildActivationResponse(currentUser, jwt, refresh, false);
    }

    private ActivationResponse buildActivationResponse(User user, String jwtToken, String refreshToken,
            boolean alreadyVerified) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        boolean profileComplete = customerProfileRepository.findByUser(user).isPresent();

        return ActivationResponse.builder()
                .userId(user.getId())
                .message(alreadyVerified ? "Already verified - logged in successfully" : "Email verified successfully!")
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .roles(roles)
                .emailVerified(true)
                .profileComplete(profileComplete)
                .alreadyVerified(alreadyVerified)
                .build();
    }

    @Override
    public void resendVerificationEmail(String email) {
        // Intentionally returns without revealing whether the email exists or is already verified
        userRepository.findUserByEmail(email).ifPresent(user -> {
            if (!user.isEmailVerified()) {
                EmailVerificationToken verificationToken = emailVerificationTokenService.generateEmailVerificationToken(user);
                emailService.sendActivationEmail(user, verificationToken.getToken());
            }
        });
    }

    @Override
    public LoginResponse refresh(RefreshRequest refreshRequest) {
        RefreshToken existing = refreshTokenService.validate(refreshRequest.refreshToken());

        User user = existing.getUser();
        if (user.getStatus() != UserStatus.ENABLED)
            throw new AccessDeniedException("Access denied");

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        int jwtVersion= tokenVersionService.getVersion(user.getId());
        String newAccess = jwtService.generateAccessToken(userDetails, user.getId(), jwtVersion);
        String newRefresh = refreshTokenService.rotate(existing);

        LoginResponse res = new LoginResponse();
        res.setUserId(user.getId());
        res.setAccessToken(newAccess);
        res.setRefreshToken(newRefresh);
        res.setAccessTokenExpiresInSeconds(15 * 60);
        res.setRoles(user.getRoles());
        res.setEmailVerified(user.getEmailVerifiedAt() != null);
        res.setCustomerProfileComplete(
                user.getRoles().contains(Role.CUSTOMER) &&
                        customerProfileRepository.findByUser(user).isPresent());
        res.setBusinessProfiles(businessAccessPolicy.getAccessibleProfiles(user));
        return res;
    }

    @Override
    public void logOut(LogoutRequest request) {
        logger.info("Logout inside auth service");
        refreshTokenService.revoke(request.refreshToken());
    }

    @Override
    public VerifyOwnershipResponse verifyOwnership(VerifyOwnershipRequest verifyOwnershipRequest) {
        String email = verifyOwnershipRequest.getEmail().trim().toLowerCase();

        Optional<User> user = userRepository.findUserByEmailIgnoreCase(email);

        if (!user.isPresent()) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(verifyOwnershipRequest.getPassword(), user.get().getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String ownershipToken = ownershipTokenService.issue(user.get().getId(), user.get().getEmail());

        VerifyOwnershipResponse res = new VerifyOwnershipResponse();
        res.setApproved(true);
        res.setEmailVerified(user.get().getEmailVerifiedAt() != null);
        res.setOwnershipToken(ownershipToken);
        return res;
    }
}
