package com.shabanaj.beloyal.business.service;

import com.shabanaj.beloyal.registration.dto.businessRegistration.BusinessRegistrationDto;
import com.shabanaj.beloyal.model.Entity.Business;

public interface BusinessService {
    Business createBusiness(BusinessRegistrationDto dto);
    void updateBusiness(Business business);
    Business getBusinessById(Long businessId);
}
