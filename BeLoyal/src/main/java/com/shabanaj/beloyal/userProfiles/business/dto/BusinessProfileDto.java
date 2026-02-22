package com.shabanaj.beloyal.userProfiles.business.dto;

import com.shabanaj.beloyal.model.Entity.Business;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessProfileDto {
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

    public BusinessProfileDto(Business business) {
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
    }
}
