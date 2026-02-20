package com.shabanaj.beloyal.registration.dto.businessMemberRegistration;

import com.shabanaj.beloyal.common.Validation.Annotation.UniqueEmailOnCreate;
import com.shabanaj.beloyal.common.Validation.Annotation.ValidEmail;
import com.shabanaj.beloyal.model.Enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class BusinessMemberInviteDto {
    @NotBlank
    @NotNull
    @ValidEmail
    @UniqueEmailOnCreate
    private String email;

    private LocalDate hireDate;

    @NotBlank
    @NotNull
    private Role role;

    public @NotBlank @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @NotNull String email) {
        this.email = email;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public @NotBlank @NotNull Role getRole() {
        return role;
    }

    public void setRole(@NotBlank @NotNull Role role) {
        this.role = role;
    }
}
