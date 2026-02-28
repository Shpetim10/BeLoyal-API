package com.shabanaj.beloyal.passwordChanger.service;

import com.shabanaj.beloyal.passwordChanger.dto.ForgetPasswordRequest;
import com.shabanaj.beloyal.passwordChanger.dto.ResetPasswordRequest;

public interface PasswordResetService {
    void generateResetToken(ForgetPasswordRequest forgetPasswordRequest);
    void validateTokenAndResetPassword(ResetPasswordRequest req);
}
