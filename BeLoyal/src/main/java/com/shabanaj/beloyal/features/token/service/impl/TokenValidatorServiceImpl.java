package com.shabanaj.beloyal.features.token.service.impl;

import com.shabanaj.beloyal.common.Exception.TokenExpiredException;
import com.shabanaj.beloyal.common.Exception.TokenIsNotValidException;
import com.shabanaj.beloyal.model.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.features.token.service.TokenValidatorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenValidatorServiceImpl implements TokenValidatorService {

    @Override
    public void validateTokenOrThrow(EmailVerificationToken token) {
        if(token.isUsed()){
            throw new TokenIsNotValidException();
        }

        if(token.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Activation token has expired");
        }
    }
}
