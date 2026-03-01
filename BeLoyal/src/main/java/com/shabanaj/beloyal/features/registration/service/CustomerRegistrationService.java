package com.shabanaj.beloyal.features.registration.service;

import com.shabanaj.beloyal.features.registration.dto.RegisterUserDto;

public interface CustomerRegistrationService {
    void createCustomer(RegisterUserDto dto);
}
