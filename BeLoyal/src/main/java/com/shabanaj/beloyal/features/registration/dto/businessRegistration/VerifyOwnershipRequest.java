package com.shabanaj.beloyal.features.registration.dto.businessRegistration;

import com.shabanaj.beloyal.common.Validation.Annotation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VerifyOwnershipRequest {
    @ValidEmail
    private String email;

    @NotBlank
    @NotNull
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public @NotBlank @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @NotNull String password) {
        this.password = password;
    }
}
