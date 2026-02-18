package com.shabanaj.beloyal.token.service;

import com.shabanaj.beloyal.model.Entity.RefreshToken;
import com.shabanaj.beloyal.model.Entity.User;

public interface RefreshTokenService {
    String create(User user, String deviceId, String userAgent);
    RefreshToken validate(String rawToken);
    String rotate(RefreshToken existing);
    void revoke(String rawToken);
    void revokeAllForUser(Long userId);
}
