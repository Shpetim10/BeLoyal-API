package com.shabanaj.beloyal.registration.dto;

import com.shabanaj.beloyal.common.Validation.Annotation.*;
import jakarta.validation.constraints.*;

public class RegisterUserDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @ValidEmail
    @UniqueEmailOnCreate
    private String email;

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

    public RegisterUserDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUsername() {
        return username;
    }


    public boolean isAcceptedTc() {
        return acceptedTc;
    }

    public String getAcceptedTcVersion() {
        return acceptedTcVersion;
    }
}
