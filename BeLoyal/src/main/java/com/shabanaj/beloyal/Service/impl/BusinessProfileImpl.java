package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.Registration.BusinessProfileRegisterDto;
import com.shabanaj.beloyal.Entity.BusinessProfile;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Repository.BusinessProfileRepository;
import com.shabanaj.beloyal.Service.BusinessProfileService;
import org.springframework.stereotype.Service;

@Service
public class BusinessProfileImpl implements BusinessProfileService {
    private final BusinessProfileRepository businessProfileRepository;

    public BusinessProfileImpl(BusinessProfileRepository businessProfileRepository) {
        this.businessProfileRepository = businessProfileRepository;
    }

    @Override
    public BusinessProfile createBusinessProfile(User user, BusinessProfileRegisterDto dto) {
        BusinessProfile businessProfile= new BusinessProfile();

        businessProfile.setUser(user);
        businessProfile.setBusinessName(dto.getBusinessName());
        businessProfile.setBusinessType(dto.getBusinessType());
        businessProfile.setBusinessDescription(dto.getBusinessDescription());
        businessProfile.setLogoUrl(dto.getLogoUrl());
        businessProfile.setAddress(dto.getAddress());
        businessProfile.setCity(dto.getCity());
        businessProfile.setCountry(dto.getCountry());
        businessProfile.setWebsiteUrl(dto.getWebsiteUrl());
        businessProfile.setVatId(dto.getVatId());
        businessProfile.setBusinessPhoneNumber(dto.getPhoneNumber());
        businessProfile.setBusinessEmail(dto.getEmail());
        businessProfile.setRating(0.0);

        return businessProfileRepository.save(businessProfile);
    }
}
