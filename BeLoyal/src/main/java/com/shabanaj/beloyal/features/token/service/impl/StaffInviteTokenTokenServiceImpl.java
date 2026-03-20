package com.shabanaj.beloyal.features.token.service.impl;

import com.shabanaj.beloyal.common.Exception.TokenIsNotValidException;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.StaffInviteToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.features.token.repository.StaffInviteTokenRepository;
import com.shabanaj.beloyal.features.token.service.StaffInviteTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffInviteTokenTokenServiceImpl implements StaffInviteTokenService {
    private final StaffInviteTokenRepository staffInviteTokenRepository;
    private final Clock clock;

    @Override
    public StaffInviteToken generateStaffInviteToken(User user, Business business, boolean isExistingUser) {
        String token = UUID.randomUUID().toString();

        StaffInviteToken staffInviteToken = new StaffInviteToken();
        staffInviteToken.setToken(token);
        staffInviteToken.setUser(user);
        staffInviteToken.setExpiresAt(LocalDateTime.now(clock).plusHours(72));
        staffInviteToken.setExistingUser(isExistingUser);
        staffInviteToken.setBusiness(business);

        return staffInviteTokenRepository.save(staffInviteToken);
    }

    @Override
    public StaffInviteToken getStaffInviteToken(String token) {
        Optional<StaffInviteToken> staffInviteToken = staffInviteTokenRepository.findByToken(token);

        if (staffInviteToken.isEmpty()) {
            throw new TokenIsNotValidException("Token could not be found");
        }

        return staffInviteToken.get();
    }

    @Override
    public void markTokenAsUsed(String token) {
        StaffInviteToken staffInviteToken = getStaffInviteToken(token);

        staffInviteToken.setUsed(true);
        staffInviteTokenRepository.save(staffInviteToken);
    }
}
