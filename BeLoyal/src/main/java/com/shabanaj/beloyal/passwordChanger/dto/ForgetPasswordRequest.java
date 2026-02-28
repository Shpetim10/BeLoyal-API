package com.shabanaj.beloyal.passwordChanger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ForgetPasswordRequest(@NotBlank @NotNull String email) {
}
