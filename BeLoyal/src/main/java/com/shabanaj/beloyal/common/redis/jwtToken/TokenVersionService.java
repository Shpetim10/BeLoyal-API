package com.shabanaj.beloyal.common.redis.jwtToken;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenVersionService {
    private final StringRedisTemplate redis;

    public TokenVersionService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public int getVersion(Long userId) {
        String key = "auth:ver:" + userId;
        String v = redis.opsForValue().get(key);
        if (v == null) {
            redis.opsForValue().set(key, "1"); // initialize
            return 1;
        }
        return Integer.parseInt(v);
    }

    public void bumpVersion(Long userId) {
        String key = "auth:ver:" + userId;
        redis.opsForValue().increment(key);
    }
}