package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Dto.Registration.businessRegistration.SubmitBusinessApplicationRequest;
import com.shabanaj.beloyal.Dto.Registration.businessRegistration.SubmitBusinessApplicationResponse;

public interface BusinessRegistrationService {
    SubmitBusinessApplicationResponse registerBusiness(SubmitBusinessApplicationRequest submitBusinessApplicationRequest);
}
