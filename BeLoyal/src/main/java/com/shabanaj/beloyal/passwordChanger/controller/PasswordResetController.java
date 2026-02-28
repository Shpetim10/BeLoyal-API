package com.shabanaj.beloyal.passwordChanger.controller;

import com.shabanaj.beloyal.passwordChanger.dto.ForgetPasswordRequest;
import com.shabanaj.beloyal.passwordChanger.dto.ResetPasswordRequest;
import com.shabanaj.beloyal.passwordChanger.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/auth")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/forget-password")
    public ResponseEntity<Map<String, String>> forgetPassword(@RequestBody @Valid ForgetPasswordRequest forgetPasswordRequest) {
        passwordResetService.generateResetToken(forgetPasswordRequest);

        return ResponseEntity.ok().body(Map.of("message", "Email was sent"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody @Valid ResetPasswordRequest request){
        passwordResetService.validateTokenAndResetPassword(request);

        return ResponseEntity.ok().body(Map.of("message", "Password has been changed"));
    }

}
