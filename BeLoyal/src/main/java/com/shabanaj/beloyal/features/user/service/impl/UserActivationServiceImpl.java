package com.shabanaj.beloyal.features.user.service.impl;

import com.shabanaj.beloyal.features.Security.CustomUserDetailsService;
import com.shabanaj.beloyal.features.Security.JwtService;
import com.shabanaj.beloyal.common.redis.jwtToken.TokenVersionService;
import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.model.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.ActivationResponse;
import com.shabanaj.beloyal.common.token.service.EmailVerificationTokenService;
import com.shabanaj.beloyal.common.token.service.RefreshTokenService;
import com.shabanaj.beloyal.common.token.service.TokenValidatorService;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.features.user.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final TokenValidatorService tokenValidatorService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final TokenVersionService tokenVersionService;

    @Override
    public ActivationResponse activateUser(String token) {
        EmailVerificationToken activationToken= emailVerificationTokenService.findEmailVerificationTokenByToken(token);

        tokenValidatorService.validateTokenOrThrow(activationToken);

        User currentUser= activationToken.getUser();

        currentUser.activateAfterEmail();

        emailVerificationTokenService.markTokenAsUsed(activationToken);

        UserDetails userDetails= customUserDetailsService.loadUserByUsername(currentUser.getEmail());
        // Generate access token
        int jwtVersion= tokenVersionService.getVersion(currentUser.getId());
        String jwt = jwtService.generateAccessToken(userDetails, currentUser.getId(), jwtVersion);
        // create refresh token
        String refresh = refreshTokenService.create(currentUser, null, null);

        currentUser.setLastLoginAt(LocalDateTime.now());
        userRepository.save(currentUser);

        return buildActivationResponse(currentUser, jwt, refresh,false);
    }

    private ActivationResponse buildActivationResponse(User user, String jwtToken, String refreshToken, boolean alreadyVerified) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        boolean profileComplete = customerProfileRepository.findByUser(user).isPresent();

        return ActivationResponse.builder()
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
}
