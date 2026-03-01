package com.shabanaj.beloyal.features.passwordChanger.service;

import com.shabanaj.beloyal.features.passwordChanger.dto.ForgetPasswordRequest;
import com.shabanaj.beloyal.features.passwordChanger.dto.ResetPasswordRequest;

public interface PasswordResetService {
    void generateResetToken(ForgetPasswordRequest forgetPasswordRequest);
    void validateTokenAndResetPassword(ResetPasswordRequest req);
}
