package com.shabanaj.beloyal.passwordChanger.dto;

public record AuthenticatedPasswordChangeResponse(
        String accessToken,
        String refreshToken
) {
}
