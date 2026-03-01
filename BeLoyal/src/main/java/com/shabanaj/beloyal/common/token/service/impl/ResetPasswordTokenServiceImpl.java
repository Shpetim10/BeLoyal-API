package com.shabanaj.beloyal.common.token.service.impl;

import com.shabanaj.beloyal.common.Exception.TokenIsNotValidException;
import com.shabanaj.beloyal.common.Exception.UserNotFound;
import com.shabanaj.beloyal.model.Entity.ResetPasswordToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.common.token.repository.ResetPasswordTokenRepository;
import com.shabanaj.beloyal.common.token.service.ResetPasswordTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordTokenServiceImpl implements ResetPasswordTokenService {
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final Clock clock;

    @Value("${app.password.token.ttl-minutes}")
    private Integer tokenTtl;

    @Override
    public ResetPasswordToken generateResetPasswordToken(User user) {
        if(user==null){
            throw new UserNotFound();
        }

        String token = UUID.randomUUID().toString();
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(token);
        resetPasswordToken.setUser(user);
        resetPasswordToken.setExpiryDate(LocalDateTime.now(clock).plusMinutes(tokenTtl));
        return resetPasswordTokenRepository.save(resetPasswordToken);
    }

    @Override
    public ResetPasswordToken getResetPasswordToken(String token) {
        if(token==null){
            throw new InvalidParameterException("Token is null");
        }

        Optional<ResetPasswordToken> optional = resetPasswordTokenRepository.findByToken(token);
        if(optional.isEmpty()){
            throw new TokenIsNotValidException("Token does not exist");
        }

        return optional.get();
    }

    @Override
    public void markTokenAsUsed(String token) {
        ResetPasswordToken resetPasswordToken = getResetPasswordToken(token);

        resetPasswordToken.setUsed(true);
        resetPasswordTokenRepository.save(resetPasswordToken);
    }
}
