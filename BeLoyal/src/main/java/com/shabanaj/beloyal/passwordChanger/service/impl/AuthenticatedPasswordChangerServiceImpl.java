package com.shabanaj.beloyal.passwordChanger.service.impl;

import com.shabanaj.beloyal.Security.JwtService;
import com.shabanaj.beloyal.common.Helpers.UserFinder;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.passwordChanger.dto.AuthenticatedPasswordChangeRequest;
import com.shabanaj.beloyal.passwordChanger.dto.AuthenticatedPasswordChangeResponse;
import com.shabanaj.beloyal.passwordChanger.service.AuthenticatedPasswordChangerService;
import com.shabanaj.beloyal.token.service.RefreshTokenService;
import com.shabanaj.beloyal.user.repository.UserRepository;
import com.shabanaj.beloyal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;

@Service
@RequiredArgsConstructor
public class AuthenticatedPasswordChangerServiceImpl implements AuthenticatedPasswordChangerService {
    private final UserFinder userFinder;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthenticatedPasswordChangeResponse changePassword(Long userId, AuthenticatedPasswordChangeRequest authenticatedPasswordChangeRequest) {
        if(authenticatedPasswordChangeRequest==null){
            throw new InvalidParameterException("authenticatedPasswordChangeRequest cannot be null");
        }

        User user= userFinder.findByIdOrThrows(userId);

        if(passwordEncoder.matches(
                authenticatedPasswordChangeRequest.oldPassword(),
                user.getPasswordHash()
        )){
            userService.changePassword(user, authenticatedPasswordChangeRequest.newPassword());
        }else{
            throw new InvalidParameterException("The old password is incorrect");
        }

        // invalidate all refresh tokens
        refreshTokenService.revokeAllForUser(userId);

        // regenerate refresh and access tokens
        String access = jwtService.generateAccessToken(user);
        String refresh = refreshTokenService.create(user, null, null);

        return new AuthenticatedPasswordChangeResponse(access, refresh);
    }
}
