package com.shabanaj.beloyal.features.passwordChanger.dto;

import com.shabanaj.beloyal.common.Validation.Annotation.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticatedPasswordChangeRequest(
        @NotNull @NotBlank String currentPassword,
        @NotNull @NotBlank @StrongPassword String newPassword
) {
}
