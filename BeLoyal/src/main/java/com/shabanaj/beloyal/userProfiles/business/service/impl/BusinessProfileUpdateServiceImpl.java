package com.shabanaj.beloyal.userProfiles.business.service.impl;

import com.shabanaj.beloyal.business.repository.BusinessRepository;
import com.shabanaj.beloyal.business.service.BusinessService;
import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.userProfiles.business.dto.BusinessProfileUpdateDto;
import com.shabanaj.beloyal.userProfiles.business.service.BusinessProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static com.shabanaj.beloyal.common.Configurations.JsonHelperConfig.*;

@Service
@RequiredArgsConstructor
public class BusinessProfileUpdateServiceImpl implements BusinessProfileUpdateService {

    private final BusinessService businessService;
    private final BusinessRepository businessRepository;

    @Override
    @Transactional
    public void updateBusinessProfile(BusinessProfileUpdateDto dto, Long businessId){
        Business business = businessService.getBusinessById(businessId);

        // Required fields: if present -> must be non-null/non-blank
        applyRequiredString(dto.getBusinessName(), "businessName", business::setBusinessName);
        applyRequiredString(dto.getCity(), "city", business::setCity);

        if (isPresent(dto.getVatId())) {
            String newVat = normalizeRequired(dto.getVatId(), "vatId");
            // Unique check
            if (!newVat.equalsIgnoreCase(safe(business.getVatId()))) {
                if (businessRepository.existsByVatIdIgnoreCase(newVat)) {
                    throw new BadRequestException("vatId already exists");
                }
                business.setVatId(newVat);
            }
        }

        // Optional fields: if present -> set (null clears)
        applyOptionalString(dto.getBusinessDescription(), business::setBusinessDescription);
        applyOptionalString(dto.getAddress(), business::setAddress);
        applyOptionalString(dto.getCountry(), business::setCountry);

        if (isPresent(dto.getWebsiteUrl())) {
            String url = normalizeOptional(dto.getWebsiteUrl());
            if (url != null && url.isBlank()) url = null;
            business.setWebsiteUrl(url);
        }

        if (isPresent(dto.getBusinessPhoneNumber())) {
            String phone = normalizeOptional(dto.getBusinessPhoneNumber());
            business.setBusinessPhoneNumber(phone);
        }

        if (isPresent(dto.getBusinessEmail())) {
            String email = normalizeOptional(dto.getBusinessEmail());
            if (email != null) email = email.toLowerCase(Locale.ROOT);
            business.setBusinessEmail(email);
        }

        if (isPresent(dto.getLogoPath()) || isPresent(dto.getLogoKey())) {
            if (isPresent(dto.getLogoPath())) business.setLogoPath(normalizeOptional(dto.getLogoPath()));
            if (isPresent(dto.getLogoKey()))  business.setLogoKey(normalizeOptional(dto.getLogoKey()));
        }

        businessRepository.save(business);
    }
}
