package com.shabanaj.beloyal.features.userProfiles.business.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.features.userProfiles.business.dto.BusinessProfileDto;
import com.shabanaj.beloyal.features.userProfiles.business.service.BusinessProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessProfileServiceImpl implements BusinessProfileService {
    private final BusinessService businessService;

    @Override
    public BusinessProfileDto getBusinessProfile(Long businessId) {
        Business business = businessService.getBusinessById(businessId);

        return new BusinessProfileDto(business);
    }
}
