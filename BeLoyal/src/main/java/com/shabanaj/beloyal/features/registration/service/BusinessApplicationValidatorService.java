package com.shabanaj.beloyal.features.registration.service;

import com.shabanaj.beloyal.features.registration.dto.businessRegistration.SubmitBusinessApplicationRequest;

public interface BusinessApplicationValidatorService {
    void validateBusinessApplicationOrThrow(SubmitBusinessApplicationRequest dto);
}
