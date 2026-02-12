package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.Entity.User;

public interface EmailVerificationTokenService {
    EmailVerificationToken generateEmailVerificationToken(User user);
    EmailVerificationToken findEmailVerificationTokenByToken(String token);
    void markTokenAsUsed(EmailVerificationToken emailVerificationToken);
}
