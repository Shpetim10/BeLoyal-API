package com.shabanaj.beloyal.userProfiles.business.service.impl;

import com.shabanaj.beloyal.business.service.BusinessService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.userProfiles.business.dto.BusinessProfileDto;
import com.shabanaj.beloyal.userProfiles.business.service.BusinessProfileService;
import org.springframework.stereotype.Service;

@Service
public class BusinessProfileServiceImpl implements BusinessProfileService {
    private BusinessService businessService;

    @Override
    public BusinessProfileDto getBusinessProfile(Long businessId) {
        Business business= businessService.getBusinessById(businessId);

        return new BusinessProfileDto(business);
    }
}
