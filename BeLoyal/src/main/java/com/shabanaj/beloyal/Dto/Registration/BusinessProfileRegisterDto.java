package com.shabanaj.beloyal.Dto.Registration;

import com.shabanaj.beloyal.Enums.BusinessType;
import com.shabanaj.beloyal.Validation.Annotation.UniqueEmailOnCreate;
import com.shabanaj.beloyal.Validation.Annotation.ValidEmail;
import com.shabanaj.beloyal.Validation.Annotation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BusinessProfileRegisterDto {
    @NotBlank
    @NotNull
    private String businessName;

    @NotEmpty
    @NotBlank
    private BusinessType businessType;

    @Size(max = 1000, message = "Business description must be less than 1000 characters")
    private String businessDescription;
    private String logoUrl;

    @NotBlank
    @NotNull
    private String address;

    @NotBlank
    @NotNull
    private String city;

    @NotBlank
    @NotNull
    private String country;
    private String websiteUrl;

    @NotBlank
    @NotNull
    private String vatId;

    @ValidPhoneNumber
    private String phoneNumber;

    @ValidEmail
    @UniqueEmailOnCreate
    private String email;

    public BusinessProfileRegisterDto(String businessName, BusinessType businessType, String businessDescription, String logoUrl, String address, String city, String country, String websiteUrl, String vatId, String phoneNumber, String email) {
        this.businessName = businessName;
        this.businessType = businessType;
        this.businessDescription = businessDescription;
        this.logoUrl = logoUrl;
        this.address = address;
        this.city = city;
        this.country = country;
        this.websiteUrl = websiteUrl;
        this.vatId = vatId;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
