package com.shabanaj.beloyal.features.customerApis.service;

import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;

public interface CustomerProfileCreationService {
    CustomerProfile createProfileForCurrentUser(CustomerProfileRegisterDto dto);
}
