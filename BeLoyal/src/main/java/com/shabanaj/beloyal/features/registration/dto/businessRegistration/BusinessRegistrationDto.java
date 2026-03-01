package com.shabanaj.beloyal.features.registration.dto.businessRegistration;

import com.shabanaj.beloyal.model.Enums.BusinessType;
import com.shabanaj.beloyal.common.Validation.Annotation.UniqueVatId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BusinessRegistrationDto {
    @NotBlank
    private String businessName;
    @NotNull
    private BusinessType businessType;
    @NotBlank
    private String city;
    @NotBlank
    private String businessPhoneNumber;
    @Email
    @NotBlank
    private String businessEmail;

    private String address;
    private String country;
    private String websiteUrl;
    private String logoUrl;
    private String logoKey;

    @NotBlank
    @NotNull
    @UniqueVatId
    private String vatId;

    @Size(max = 1000)
    private String businessDescription;

    public @NotBlank String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(@NotBlank String businessName) {
        this.businessName = businessName;
    }

    public @NotNull BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(@NotNull BusinessType businessType) {
        this.businessType = businessType;
    }

    public @NotBlank String getCity() {
        return city;
    }

    public void setCity(@NotBlank String city) {
        this.city = city;
    }

    public @NotBlank String getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    public void setBusinessPhoneNumber(@NotBlank String businessPhoneNumber) {
        this.businessPhoneNumber = businessPhoneNumber;
    }

    public @Email @NotBlank String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(@Email @NotBlank String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLogoKey() {
        return logoKey;
    }

    public void setLogoKey(String logoKey) {
        this.logoKey = logoKey;
    }

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }

    public @Size(max = 1000) String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(@Size(max = 1000) String businessDescription) {
        this.businessDescription = businessDescription;
    }
}
