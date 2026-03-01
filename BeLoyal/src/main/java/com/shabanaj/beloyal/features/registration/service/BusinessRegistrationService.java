package com.shabanaj.beloyal.features.registration.service;

import com.shabanaj.beloyal.features.registration.dto.businessRegistration.SubmitBusinessApplicationRequest;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.SubmitBusinessApplicationResponse;

public interface BusinessRegistrationService {
    SubmitBusinessApplicationResponse registerBusiness(SubmitBusinessApplicationRequest submitBusinessApplicationRequest);
}
