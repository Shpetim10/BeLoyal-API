package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.Registration.BusinessProfileRegisterDto;
import com.shabanaj.beloyal.Entity.Business;
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
    public Business createBusinessProfile(User user, BusinessProfileRegisterDto dto) {
        Business business = new Business();

        business.setBusinessName(dto.getBusinessName());
        business.setBusinessType(dto.getBusinessType());
        business.setBusinessDescription(dto.getBusinessDescription());
        business.setLogoUrl(dto.getLogoUrl());
        business.setAddress(dto.getAddress());
        business.setCity(dto.getCity());
        business.setCountry(dto.getCountry());
        business.setWebsiteUrl(dto.getWebsiteUrl());
        business.setVatId(dto.getVatId());
        business.setBusinessPhoneNumber(dto.getPhoneNumber());
        business.setBusinessEmail(dto.getEmail());
        business.setRating(0.0);

        return businessProfileRepository.save(business);
    }
}
