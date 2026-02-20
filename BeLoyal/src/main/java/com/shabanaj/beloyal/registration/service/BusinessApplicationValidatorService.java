package com.shabanaj.beloyal.registration.service;

import com.shabanaj.beloyal.registration.dto.businessRegistration.SubmitBusinessApplicationRequest;
import com.shabanaj.beloyal.registration.dto.customerRegistraton.RegisterUserDto;

public interface BusinessApplicationValidatorService {
    void validateBusinessApplicationOrThrow(SubmitBusinessApplicationRequest dto);
}
