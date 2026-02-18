package com.shabanaj.beloyal.registration.dto.businessRegistration;

import com.shabanaj.beloyal.registration.dto.customerRegistraton.RegisterUserDto;
import com.shabanaj.beloyal.model.Enums.OwnerMode;
import jakarta.validation.constraints.NotNull;

public class SubmitBusinessApplicationRequest {
    // business info
    @NotNull
    private BusinessRegistrationDto businessRegistrationDto;

    // Flag for detecting Owner mode
    @NotNull
    private OwnerMode ownerMode;

    // If user exists
    private String ownershipToken;

    // only for NEW_ACCOUNT
    private RegisterUserDto userDto;

    public SubmitBusinessApplicationRequest() {
    }

    public @NotNull BusinessRegistrationDto getBusinessRegistrationDto() {
        return businessRegistrationDto;
    }

    public void setBusinessRegistrationDto(@NotNull BusinessRegistrationDto businessRegistrationDto) {
        this.businessRegistrationDto = businessRegistrationDto;
    }

    public @NotNull OwnerMode getOwnerMode() {
        return ownerMode;
    }

    public void setOwnerMode(@NotNull OwnerMode ownerMode) {
        this.ownerMode = ownerMode;
    }

    public String getOwnershipToken() {
        return ownershipToken;
    }

    public void setOwnershipToken(String ownershipToken) {
        this.ownershipToken = ownershipToken;
    }

    public RegisterUserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(RegisterUserDto userDto) {
        this.userDto = userDto;
    }
}
