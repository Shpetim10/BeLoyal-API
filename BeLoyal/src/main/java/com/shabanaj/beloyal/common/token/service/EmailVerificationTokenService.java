package com.shabanaj.beloyal.common.token.service;

import com.shabanaj.beloyal.model.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.model.Entity.User;

public interface EmailVerificationTokenService {
    EmailVerificationToken generateEmailVerificationToken(User user);
    EmailVerificationToken findEmailVerificationTokenByToken(String token);
    void markTokenAsUsed(EmailVerificationToken emailVerificationToken);
}
