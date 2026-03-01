package com.shabanaj.beloyal.features.auth.service.impl;

import com.shabanaj.beloyal.features.Security.CustomUserDetailsService;
import com.shabanaj.beloyal.features.Security.JwtService;
import com.shabanaj.beloyal.features.auth.dto.BusinessProfileInfo;
import com.shabanaj.beloyal.features.auth.dto.LoginResponse;
import com.shabanaj.beloyal.features.auth.service.TokenIssuerService;
import com.shabanaj.beloyal.common.redis.jwtToken.TokenVersionService;
import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.common.token.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenIssuerServiceImpl implements TokenIssuerService {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomerProfileRepository customerProfileRepository;
    private final TokenVersionService tokenVersionService;

    public LoginResponse issue(User user, List<BusinessProfileInfo> businessProfiles) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        int tokenVersion= tokenVersionService.getVersion(user.getId());
        String access = jwtService.generateAccessToken(userDetails,user.getId(), tokenVersion);
        String refresh = refreshTokenService.create(user, null, null);

        LoginResponse res = new LoginResponse();
        res.setUserId(user.getId());
        res.setAccessToken(access);
        res.setRefreshToken(refresh);
        res.setAccessTokenExpiresInSeconds(15 * 60);
        res.setRoles(user.getRoles());
        res.setEmailVerified(user.getEmailVerifiedAt() != null);

        res.setCustomerProfileComplete(
                user.getRoles().contains(Role.CUSTOMER) &&
                        customerProfileRepository.findByUser(user).isPresent());

        res.setBusinessProfiles(businessProfiles);
        return res;
    }
}
