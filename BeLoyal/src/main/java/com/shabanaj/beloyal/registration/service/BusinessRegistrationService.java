package com.shabanaj.beloyal.registration.service;

import com.shabanaj.beloyal.registration.dto.businessRegistration.SubmitBusinessApplicationRequest;
import com.shabanaj.beloyal.registration.dto.businessRegistration.SubmitBusinessApplicationResponse;

public interface BusinessRegistrationService {
    SubmitBusinessApplicationResponse registerBusiness(SubmitBusinessApplicationRequest submitBusinessApplicationRequest);
}
