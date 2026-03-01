package com.shabanaj.beloyal.features.business.service;

import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.BusinessRegistrationDto;
import com.shabanaj.beloyal.model.Entity.Business;

import java.util.List;

public interface BusinessService {
    Business createBusiness(BusinessRegistrationDto dto);
    void updateBusiness(Business business);
    Business getBusinessById(Long businessId);
    List<Business> getPendingBusinesses();
    List<Business> getBusinessesByStatus(BusinessStatus status);
    List<Business> getAllBusinesses();
}
