package com.shabanaj.beloyal.features.auth.service.impl;

import com.shabanaj.beloyal.features.auth.service.AuthenticationAttemptService;
import com.shabanaj.beloyal.common.Configurations.SystemSettings;
import com.shabanaj.beloyal.common.Exception.InvalidCredentialsException;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationAttemptServiceImpl implements AuthenticationAttemptService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final Clock clock;
    private final SystemSettings settings;

    @Override
    public void authenticateOrThrow(User user, String email, String password) {
        LocalDateTime now = LocalDateTime.now(clock);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException ex) {
            user.recordFailedLogin(now, settings.getMAX_LOGIN_ATTEMPTS(), settings.getLOCK_MINUTES());
            userRepository.save(user);
            throw new InvalidCredentialsException();
        }
    }
}
