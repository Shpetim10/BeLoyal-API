package com.shabanaj.beloyal.features.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class OwnershipTokenService {
    @Value("${app.ownership.jwt.secret}")
    private String ownershipSecret;

    @Value("${app.ownership.jwt.ttl-minutes:10}")
    private long ttlMinutes;

    public String issue(Long userId, String email) {
        Instant now = Instant.now();
        Instant exp = now.plus(Duration.ofMinutes(ttlMinutes));

        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "OWNERSHIP");
        claims.put("purpose", "BUSINESS_REG_EXISTING");
        claims.put("userId", userId);
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(Keys.hmacShaKeyFor(ownershipSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public Claims verify(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(ownershipSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        // hard checks so this token can’t be used elsewhere
        String type = claims.get("token_type", String.class);
        String purpose = claims.get("purpose", String.class);

        if (!"OWNERSHIP".equals(type)) {
            throw new RuntimeException("Invalid token type");
        }
        if (!"BUSINESS_REG_EXISTING".equals(purpose)) {
            throw new RuntimeException("Invalid token purpose");
        }
        if (claims.get("userId") == null) {
            throw new RuntimeException("Token missing userId");
        }

        return claims;
    }
}
