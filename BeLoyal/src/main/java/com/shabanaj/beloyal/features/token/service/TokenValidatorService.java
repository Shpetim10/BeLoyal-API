package com.shabanaj.beloyal.features.token.service;

import com.shabanaj.beloyal.model.Entity.EmailVerificationToken;

public interface TokenValidatorService {
    void validateTokenOrThrow(EmailVerificationToken token);
}
