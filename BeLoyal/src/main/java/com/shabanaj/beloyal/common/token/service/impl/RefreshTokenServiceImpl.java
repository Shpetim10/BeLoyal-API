package com.shabanaj.beloyal.common.token.service.impl;

import com.shabanaj.beloyal.model.Entity.RefreshToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.common.Helpers.TokenUtil;
import com.shabanaj.beloyal.common.token.repository.RefreshTokenRepository;
import com.shabanaj.beloyal.common.token.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    @Value("${app.refresh-ttl-days:30}")
    private int refreshTtlDays;

    public RefreshTokenServiceImpl(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    @Override
    public String create(User user, String deviceId, String userAgent) {
        String raw = TokenUtil.generateOpaqueToken();
        String hash = TokenUtil.sha256Hex(raw);

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(hash);
        rt.setCreatedAt(LocalDateTime.now());
        rt.setExpiresAt(LocalDateTime.now().plusDays(refreshTtlDays));
        rt.setRevokedAt(null);
        rt.setDeviceId(deviceId);
        rt.setUserAgent(userAgent);

        repo.save(rt);
        return raw;
    }

    @Override
    public RefreshToken validate(String rawToken) {
        String hash = TokenUtil.sha256Hex(rawToken);
        RefreshToken rt = repo.findByTokenHash(hash)
                .orElseThrow(() -> new AccessDeniedException("Invalid refresh token"));

        LocalDateTime now = LocalDateTime.now();
        if (rt.isRevoked() || rt.isExpired(now)) {
            throw new AccessDeniedException("Invalid refresh token");
        }
        return rt;
    }

    @Override
    @Transactional
    public String rotate(RefreshToken existing) {
        existing.setRevokedAt(LocalDateTime.now());
        repo.save(existing);

        return create(existing.getUser(), existing.getDeviceId(), existing.getUserAgent());
    }

    @Override
    public void revoke(String rawToken) {
        logger.info("Revoking refresh token inside refresh token service");
        String hash = TokenUtil.sha256Hex(rawToken);
        repo.findByTokenHash(hash).ifPresent(rt -> {
            if (!rt.isRevoked()) {
                rt.setRevokedAt(LocalDateTime.now());
                repo.save(rt);
                logger.info("Revoked refresh token inside refresh token service and persisted");
            }
        });
    }

    @Override
    public void revokeAllForUser(Long userId) {
        for (RefreshToken rt : repo.findAllByUserIdAndRevokedAtIsNull(userId)) {
            rt.setRevokedAt(LocalDateTime.now());
            repo.save(rt);
        }
    }
}
