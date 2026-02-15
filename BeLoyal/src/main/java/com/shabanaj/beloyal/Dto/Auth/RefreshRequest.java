package com.shabanaj.beloyal.Dto.Auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank String refreshToken, @NotBlank String accessToken) {
}
