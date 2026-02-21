package com.shabanaj.beloyal.auth.service.impl;

import com.shabanaj.beloyal.auth.dto.BusinessProfileInfo;
import com.shabanaj.beloyal.auth.dto.LoginRequest;
import com.shabanaj.beloyal.auth.dto.LoginResponse;
import com.shabanaj.beloyal.auth.policy.BusinessAccessPolicy;
import com.shabanaj.beloyal.auth.policy.LoginPolicy;
import com.shabanaj.beloyal.auth.service.AuthenticationAttemptService;
import com.shabanaj.beloyal.auth.service.LoginService;
import com.shabanaj.beloyal.auth.service.TokenIssuerService;
import com.shabanaj.beloyal.common.Helpers.EmailNormalizer;
import com.shabanaj.beloyal.common.Helpers.UserFinder;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final EmailNormalizer emailNormalizer;
    private final UserFinder userFinder;
    private final LoginPolicy loginPolicy;
    private final BusinessAccessPolicy businessAccessPolicy;
    private final AuthenticationAttemptService authenticationAttemptService;
    private final Clock clock;
    private final UserRepository userRepository;
    private final TokenIssuerService tokenIssuer;

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = emailNormalizer.normalizeEmail(request.getEmail());
        User user = userFinder.findByEmailOrThrows(email);

        loginPolicy.validateOrThrow(user);
        List<BusinessProfileInfo> accessibleBusinessProfiles= businessAccessPolicy.getAccessibleProfiles(user);

        authenticationAttemptService.authenticateOrThrow(user, email, request.getPassword());

        user.recordSuccessfulLogin(LocalDateTime.now(clock));
        userRepository.save(user);

        return tokenIssuer.issue(user, accessibleBusinessProfiles);
    }
}
