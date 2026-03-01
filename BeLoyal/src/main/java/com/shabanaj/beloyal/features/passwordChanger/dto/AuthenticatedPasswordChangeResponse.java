package com.shabanaj.beloyal.features.passwordChanger.dto;

public record AuthenticatedPasswordChangeResponse(
        String accessToken,
        String refreshToken
) {
}
