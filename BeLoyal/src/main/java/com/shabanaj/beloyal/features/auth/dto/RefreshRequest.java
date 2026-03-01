package com.shabanaj.beloyal.features.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank String refreshToken, String accessToken) {
}
