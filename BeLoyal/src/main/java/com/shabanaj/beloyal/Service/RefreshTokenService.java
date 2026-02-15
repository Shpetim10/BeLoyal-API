package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Entity.RefreshToken;
import com.shabanaj.beloyal.Entity.User;

public interface RefreshTokenService {
    String create(User user, String deviceId, String userAgent);
    RefreshToken validate(String rawToken);
    String rotate(RefreshToken existing);
    void revoke(String rawToken);
    void revokeAllForUser(Long userId);
}
