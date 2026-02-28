package com.shabanaj.beloyal.passwordChanger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticatedPasswordChangeRequest(
        @NotNull @NotBlank String oldPassword,
        @NotNull @NotBlank String newPassword
) {
}
