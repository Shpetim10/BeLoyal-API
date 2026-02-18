package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Dto.Registration.businessRegistration.BusinessRegistrationDto;
import com.shabanaj.beloyal.Entity.Business;

public interface BusinessService {
    Business createBusiness(BusinessRegistrationDto dto);
    void updateBusiness(Business business);
    Business getBusinessById(Long businessId);
}
