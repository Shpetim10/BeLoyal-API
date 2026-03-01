package com.shabanaj.beloyal.features.user.dto;

import com.shabanaj.beloyal.common.Validation.Annotation.*;
import jakarta.validation.constraints.NotBlank;

public class UpdateUserDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @ValidEmail
    @UniqueEmailOnCreate
    private String email;

    @ValidPhoneNumber
    @UniquePhoneNumberOnCreate
    private String phoneNumber;

    @UniqueUsernameOnCreate
    private String username;

    private String profileImage;

    public UpdateUserDto() {
    }

    public UpdateUserDto(String firstName, String lastName, String email, String password, String phoneNumber, String username, String profileImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.profileImage = profileImage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
