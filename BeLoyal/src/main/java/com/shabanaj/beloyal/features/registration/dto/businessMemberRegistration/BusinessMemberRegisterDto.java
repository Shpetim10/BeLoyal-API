package com.shabanaj.beloyal.features.registration.dto.businessMemberRegistration;

import com.shabanaj.beloyal.common.Validation.Annotation.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BusinessMemberRegisterDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @NotBlank
    @NotNull
    @StrongPassword
    private String password;

    @ValidPhoneNumber
    @UniquePhoneNumberOnCreate
    private String phoneNumber;

    @UniqueUsernameOnCreate
    private String username;

    // T&C acceptance
    @AssertTrue(message = "Terms & Conditions must be accepted")
    private boolean acceptedTc;

    @NotBlank
    @Size(max = 50)
    private String acceptedTcVersion;

    @NotBlank
    @NotNull
    private String token;

    public @NotBlank String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank String lastName) {
        this.lastName = lastName;
    }

    public @NotBlank @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @NotNull String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public @AssertTrue(message = "Terms & Conditions must be accepted") boolean isAcceptedTc() {
        return acceptedTc;
    }

    public void setAcceptedTc(@AssertTrue(message = "Terms & Conditions must be accepted") boolean acceptedTc) {
        this.acceptedTc = acceptedTc;
    }

    public @NotBlank @Size(max = 50) String getAcceptedTcVersion() {
        return acceptedTcVersion;
    }

    public void setAcceptedTcVersion(@NotBlank @Size(max = 50) String acceptedTcVersion) {
        this.acceptedTcVersion = acceptedTcVersion;
    }

    public @NotBlank @NotNull String getToken() {
        return token;
    }

    public void setToken(@NotBlank @NotNull String token) {
        this.token = token;
    }
}
