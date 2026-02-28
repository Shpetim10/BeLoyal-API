package com.shabanaj.beloyal.passwordChanger.service.impl;

import com.shabanaj.beloyal.common.Exception.TokenIsNotValidException;
import com.shabanaj.beloyal.common.Helpers.UserFinder;
import com.shabanaj.beloyal.email.service.EmailService;
import com.shabanaj.beloyal.model.Entity.ResetPasswordToken;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.passwordChanger.dto.ForgetPasswordRequest;
import com.shabanaj.beloyal.passwordChanger.dto.ResetPasswordRequest;
import com.shabanaj.beloyal.passwordChanger.service.PasswordResetService;
import com.shabanaj.beloyal.token.service.ResetPasswordTokenService;
import com.shabanaj.beloyal.user.repository.UserRepository;
import com.shabanaj.beloyal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private final ResetPasswordTokenService resetPasswordTokenService;
    private final UserFinder userFinder;
    private final EmailService emailService;
    private final Clock clock;
    private final UserService userService;

    @Override
    @Transactional
    public void generateResetToken(ForgetPasswordRequest forgetPasswordRequest) {
        if(forgetPasswordRequest==null){
            throw new InvalidParameterException("forgetPasswordRequest cannot be null");
        }
        User user= userFinder.findByEmailOrThrows(forgetPasswordRequest.email());

        // generate token
        ResetPasswordToken resetToken= resetPasswordTokenService.generateResetPasswordToken(user);

        //send email
        emailService.sendForgetPasswordEmail(user, resetToken.getToken());
    }

    @Override
    @Transactional
    public void validateTokenAndResetPassword(ResetPasswordRequest req) {
        if(req==null){
            throw new InvalidParameterException("ResetPasswordRequest cannot be null");
        }
        // get or throw
        ResetPasswordToken resetPasswordToken= resetPasswordTokenService.getResetPasswordToken(
                req.token()
        );

        if(resetPasswordToken.getExpiryDate().isBefore(LocalDateTime.now(clock))){
            throw new TokenIsNotValidException("Token has expired");
        }
        // mark token as used
        resetPasswordTokenService.markTokenAsUsed(resetPasswordToken.getToken());

        // reset password
        User user=resetPasswordToken.getUser();
        userService.changePassword(user, req.newPassword());
    }
}
