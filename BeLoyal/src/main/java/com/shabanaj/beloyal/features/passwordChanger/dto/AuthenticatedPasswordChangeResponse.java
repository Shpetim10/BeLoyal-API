package com.shabanaj.beloyal.features.passwordChanger.dto;

public record AuthenticatedPasswordChangeResponse(
        String message,
        String accessToken,
        String refreshToken
) {
}
