package com.shabanaj.beloyal.auth.service.impl;

import com.shabanaj.beloyal.Security.CustomUserDetailsService;
import com.shabanaj.beloyal.Security.JwtService;
import com.shabanaj.beloyal.auth.dto.BusinessProfileInfo;
import com.shabanaj.beloyal.auth.dto.LoginResponse;
import com.shabanaj.beloyal.auth.service.TokenIssuerService;
import com.shabanaj.beloyal.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.token.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenIssuerServiceImpl implements TokenIssuerService {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomerProfileRepository customerProfileRepository;

    public LoginResponse issue(User user, List<BusinessProfileInfo> businessProfiles) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String access = jwtService.generateAccessToken(userDetails);
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
