package com.shabanaj.beloyal.business.service.impl;

import com.shabanaj.beloyal.registration.dto.businessRegistration.BusinessRegistrationDto;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import com.shabanaj.beloyal.common.Exception.BusinessNotFound;
import com.shabanaj.beloyal.business.repository.BusinessRepository;
import com.shabanaj.beloyal.business.service.BusinessService;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessServiceImpl implements BusinessService {
    private final BusinessRepository businessRepository;

    public BusinessServiceImpl(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    @Override
    public Business createBusiness(BusinessRegistrationDto dto) {
        if(dto==null){
            throw new InvalidParameterException("BusinessRegistrationDto is null");
        }

        Business business = new Business();
        business.setBusinessName(dto.getBusinessName());
        business.setBusinessType(dto.getBusinessType());
        business.setBusinessDescription(dto.getBusinessDescription());
        business.setLogoPath(dto.getLogoUrl());
        business.setAddress(dto.getAddress());
        business.setCity(dto.getCity());
        business.setCountry(dto.getCountry());
        business.setWebsiteUrl(dto.getWebsiteUrl());
        business.setVatId(dto.getVatId());
        business.setBusinessPhoneNumber(dto.getBusinessPhoneNumber());
        business.setBusinessEmail(dto.getBusinessEmail());
        business.setBusinessStatus(BusinessStatus.PENDING_APPROVAL);
        business.setSubmittedAt(LocalDateTime.now());

        return businessRepository.save(business);
    }

    @Override
    public void updateBusiness(Business business) {

    }

    @Override
    public Business getBusinessById(Long businessId) {
        Optional<Business> business=businessRepository.findById(businessId);

        if(business.isEmpty()){
            throw new BusinessNotFound();
        }

        return business.get();
    }

    @Override
    public List<Business> getPendingBusinesses() {
        return businessRepository.getBusinessesByBusinessStatus(BusinessStatus.PENDING_APPROVAL);
    }
}
