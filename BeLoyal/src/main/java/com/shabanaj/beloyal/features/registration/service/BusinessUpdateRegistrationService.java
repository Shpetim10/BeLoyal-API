package com.shabanaj.beloyal.features.registration.service;

import com.shabanaj.beloyal.features.registration.dto.businessRegistration.BusinessRegistrationDto;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.SubmitBusinessApplicationResponse;

public interface BusinessUpdateRegistrationService {
    SubmitBusinessApplicationResponse update(Long businessId, BusinessRegistrationDto dto);
    BusinessRegistrationDto get(Long businessId);
}
