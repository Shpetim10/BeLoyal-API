package com.shabanaj.beloyal.Dto;

import com.shabanaj.beloyal.Enums.Role;
import com.shabanaj.beloyal.Validation.Annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

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
    private String password;

    @ValidPhoneNumber
    @UniquePhoneNumberOnCreate
    private String phoneNumber;

    @UniqueUsernameOnCreate
    private String username;

    private String profileImageUrl;

    @NotEmpty
    private Set<Role> roles;

    public RegisterUserDto() {
    }

    public RegisterUserDto(String firstName, String lastName, String email, String password, String phoneNumber, String username, String profileImageUrl, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.roles = roles;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
