package com.shabanaj.beloyal.features.passwordChanger.dto;

import com.shabanaj.beloyal.common.Validation.Annotation.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest(
        @NotBlank @NotNull String token,
        @NotNull @NotBlank @StrongPassword String newPassword) {
}
