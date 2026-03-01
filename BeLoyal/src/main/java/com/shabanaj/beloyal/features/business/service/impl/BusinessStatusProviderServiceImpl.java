package com.shabanaj.beloyal.features.business.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.business.service.BusinessStatusProviderService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessStatusProviderServiceImpl implements BusinessStatusProviderService {
    private final BusinessService businessService;

    @Override
    public BusinessStatus getBusinessStatus(Long businessId) {
        Business business = businessService.getBusinessById(businessId);
        if(business==null){
            return null;
        }
        return business.getBusinessStatus();
    }
}
