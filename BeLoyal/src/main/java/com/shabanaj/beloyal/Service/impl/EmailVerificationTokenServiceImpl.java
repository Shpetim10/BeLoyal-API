package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Entity.EmailVerificationToken;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Exception.TokenIsNotValidException;
import com.shabanaj.beloyal.Repository.VerificationTokenRepository;
import com.shabanaj.beloyal.Service.EmailVerificationTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public EmailVerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public EmailVerificationToken generateEmailVerificationToken(User user) {
        String token= UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public EmailVerificationToken findEmailVerificationTokenByToken(String token) {
        EmailVerificationToken activationToken= verificationTokenRepository.findByToken(token)
                .orElseThrow(()->new TokenIsNotValidException("Token is not found!"));

        return activationToken;
    }

    @Override
    public void markTokenAsUsed(EmailVerificationToken emailVerificationToken) {
        emailVerificationToken.setUsed(true);
        verificationTokenRepository.save(emailVerificationToken);
    }
}
