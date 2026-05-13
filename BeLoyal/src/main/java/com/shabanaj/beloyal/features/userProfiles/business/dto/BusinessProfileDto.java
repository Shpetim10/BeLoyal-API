package com.shabanaj.beloyal.features.userProfiles.business.dto;

import com.shabanaj.beloyal.model.Entity.Business;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessProfileDto {
    private Long id;
    private String businessName;
    private String businessType;
    private String businessDescription;
    private String address;
    private String city;
    private String country;
    private String websiteUrl;
    private String vatId;
    private String businessPhoneNumber;
    private String businessEmail;
    private String businessStatus;
    private String logoPath;
    private String logoKey;
    private String currencyCode;

    public BusinessProfileDto(Business business) {
        this.id = business.getId();
        this.businessName = business.getBusinessName();
        this.businessType = business.getBusinessType().name();
        this.businessDescription = business.getBusinessDescription();
        this.address = business.getAddress();
        this.city = business.getCity();
        this.country = business.getCountry();
        this.websiteUrl = business.getWebsiteUrl();
        this.vatId = business.getVatId();
        this.businessPhoneNumber = business.getBusinessPhoneNumber();
        this.businessEmail = business.getBusinessEmail();
        this.businessStatus = business.getBusinessStatus().name();
        this.logoPath = business.getLogoPath();
        this.logoKey = business.getLogoKey();
        this.currencyCode = business.getCurrencyCode() != null ? business.getCurrencyCode().name() : null;
    }
}
