package com.shabanaj.beloyal.registration.service;

import com.shabanaj.beloyal.registration.dto.businessRegistration.SubmitBusinessApplicationRequest;

public interface BusinessApplicationValidatorService {
    void validateBusinessApplicationOrThrow(SubmitBusinessApplicationRequest dto);
}
