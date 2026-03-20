package com.shabanaj.beloyal.features.token.service.impl;

import com.shabanaj.beloyal.features.token.repository.RefreshTokenRepository;
import com.shabanaj.beloyal.features.token.service.RefreshTokenCleanUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenCleanUpServiceImpl implements RefreshTokenCleanUpService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    @Override
    @Transactional
    public void cleanUpRevokedTokens() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime revokedCutoff = now.minusDays(7); // tolerance for revokedAt
        LocalDateTime expiredCutoff  = now.minusDays(1); // tolerance for expiredAt

        refreshTokenRepository.deleteOldTokens(revokedCutoff, expiredCutoff);
    }
}
