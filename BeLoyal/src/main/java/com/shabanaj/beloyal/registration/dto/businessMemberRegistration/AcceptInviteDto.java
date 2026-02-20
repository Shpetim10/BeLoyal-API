package com.shabanaj.beloyal.registration.dto.businessMemberRegistration;

import jakarta.validation.constraints.NotBlank;

public class AcceptInviteDto {
    @NotBlank
    private String token;

    public @NotBlank String getToken() {
        return token;
    }

    public void setToken(@NotBlank String token) {
        this.token = token;
    }
}