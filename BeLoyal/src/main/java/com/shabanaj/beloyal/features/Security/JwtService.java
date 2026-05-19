package com.shabanaj.beloyal.features.Security;

import com.shabanaj.beloyal.model.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    // store a BASE64-encoded secret in properties
    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.access-ttl-minutes}") // e.g. 15 minutes
    private long accessTtl;

    // Generate jwt token methods
    public String generateAccessToken(UserDetails userDetails, Long userId, int tokenVersion) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTtl * 60_000);

        // set claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("ver", tokenVersion);
        claims.put("token_type", "ACCESS");

        return Jwts.builder()
                .setClaims(claims) // set custom claims FIRST
                .setSubject(userDetails.getUsername()) // subject AFTER, won't be overwritten
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Helpers

    private Key getSigningKey() {
        // if secretKey is base64-encoded:
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object uid = extractAllClaims(token).get("uid");
        if (uid == null)
            return null;
        if (uid instanceof Integer i)
            return i.longValue();
        if (uid instanceof Long l)
            return l;
        return Long.parseLong(uid.toString());
    }

    public Integer extractTokenVersion(String token) {
        Object ver = extractAllClaims(token).get("ver");
        if (ver == null)
            return null;
        if (ver instanceof Integer i)
            return i;
        if (ver instanceof Long l)
            return l.intValue();
        return Integer.parseInt(ver.toString());
    }

    public String extractTokenType(String token) {
        Object type = extractAllClaims(token).get("token_type");
        return type != null ? type.toString() : null;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
