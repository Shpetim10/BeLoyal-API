package com.shabanaj.beloyal.common.token.service;

import com.shabanaj.beloyal.model.Entity.ResetPasswordToken;
import com.shabanaj.beloyal.model.Entity.User;

public interface ResetPasswordTokenService {
    ResetPasswordToken generateResetPasswordToken(User user);

    ResetPasswordToken getResetPasswordToken(String token);

    void markTokenAsUsed(String token);
}
