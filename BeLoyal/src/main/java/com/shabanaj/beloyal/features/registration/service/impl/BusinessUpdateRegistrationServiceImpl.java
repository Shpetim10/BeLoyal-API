package com.shabanaj.beloyal.features.registration.service.impl;

import com.shabanaj.beloyal.features.business.repository.BusinessRepository;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.BusinessRegistrationDto;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.SubmitBusinessApplicationResponse;
import com.shabanaj.beloyal.features.registration.service.BusinessUpdateRegistrationService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessUpdateRegistrationServiceImpl implements BusinessUpdateRegistrationService {
    private final BusinessRepository businessRepository;
    private final BusinessService businessService;

    @Override
    @Transactional
    public SubmitBusinessApplicationResponse update(Long businessId, BusinessRegistrationDto dto) {
        if(businessId == null || dto == null) {
            throw new IllegalArgumentException("businessId and dto cannot be null");
        }

        Business business= businessService.getBusinessById(businessId);

        if(business.getBusinessStatus().equals(BusinessStatus.ACTIVE)) {
            throw new IllegalArgumentException("The registration is already approved!");
        }

        business.setBusinessName(dto.getBusinessName());
        business.setBusinessType(dto.getBusinessType());
        business.setCity(dto.getCity());
        business.setBusinessPhoneNumber(String.valueOf(dto.getBusinessPhoneNumber()));
        business.setBusinessEmail(dto.getBusinessEmail());
        business.setAddress(dto.getAddress());
        business.setCountry(dto.getCountry());
        business.setWebsiteUrl(dto.getWebsiteUrl());
        business.setLogoPath(dto.getLogoUrl());
        business.setLogoKey(dto.getLogoKey());
        business.setVatId(dto.getVatId());
        business.setBusinessDescription(dto.getBusinessDescription());

        business.setBusinessStatus(BusinessStatus.PENDING_APPROVAL);

        businessRepository.save(business);

        return new SubmitBusinessApplicationResponse(
                businessId,
                business.getBusinessStatus(),
                "Your application was updated!"
        );
    }

    @Override
    public BusinessRegistrationDto get(Long businessId) {
        Business business= businessService.getBusinessById(businessId);

        BusinessRegistrationDto businessRegistrationDto = new BusinessRegistrationDto();

        businessRegistrationDto.setBusinessName(business.getBusinessName());
        businessRegistrationDto.setBusinessType(business.getBusinessType());
        businessRegistrationDto.setCity(business.getCity());
        businessRegistrationDto.setBusinessPhoneNumber(business.getBusinessPhoneNumber());
        businessRegistrationDto.setBusinessEmail(business.getBusinessEmail());
        businessRegistrationDto.setAddress(business.getAddress());
        businessRegistrationDto.setCountry(business.getCountry());
        businessRegistrationDto.setWebsiteUrl(business.getWebsiteUrl());
        businessRegistrationDto.setLogoKey(business.getLogoKey());
        businessRegistrationDto.setLogoUrl(business.getLogoPath());
        businessRegistrationDto.setVatId(business.getVatId());
        businessRegistrationDto.setBusinessDescription(business.getBusinessDescription());

        return businessRegistrationDto;
    }
}
