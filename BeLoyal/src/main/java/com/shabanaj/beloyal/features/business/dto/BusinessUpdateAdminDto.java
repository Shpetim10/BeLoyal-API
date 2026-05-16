package com.shabanaj.beloyal.features.business.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shabanaj.beloyal.model.Enums.BusinessCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessUpdateAdminDto {
    private String businessName;

    private BusinessCategory businessType;

    @Size(max = 1000)
    private String businessDescription;

    private String address;

    private String city;

    private String country;

    private String websiteUrl;

    private String businessPhoneNumber;

    @Email
    private String businessEmail;

    private String logoPath;

    private String logoKey;
}
