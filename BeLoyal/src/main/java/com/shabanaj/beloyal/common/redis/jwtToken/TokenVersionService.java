package com.shabanaj.beloyal.common.redis.jwtToken;

import com.shabanaj.beloyal.features.user.repository.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenVersionService {
    private final StringRedisTemplate redis;
    private final UserRepository userRepository;

    public TokenVersionService(StringRedisTemplate redis, UserRepository userRepository) {
        this.redis = redis;
        this.userRepository = userRepository;
    }

    public int getVersion(Long userId) {
        String key = "auth:ver:" + userId;
        String v = redis.opsForValue().get(key);
        if (v == null) {
            // Fall back to the DB-persisted version so a Redis flush doesn't re-enable revoked tokens
            int dbVersion = userRepository.findById(userId)
                    .map(u -> u.getTokenVersion())
                    .orElse(1);
            redis.opsForValue().set(key, String.valueOf(dbVersion));
            return dbVersion;
        }
        return Integer.parseInt(v);
    }

    @Transactional
    public void bumpVersion(Long userId) {
        String key = "auth:ver:" + userId;
        redis.opsForValue().increment(key);
        // Persist to DB so the incremented version survives a Redis flush
        userRepository.findById(userId).ifPresent(user -> {
            user.setTokenVersion(user.getTokenVersion() + 1);
            userRepository.save(user);
        });
    }
}