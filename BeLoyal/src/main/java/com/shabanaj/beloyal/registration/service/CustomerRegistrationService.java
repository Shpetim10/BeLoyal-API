package com.shabanaj.beloyal.registration.service;

import com.shabanaj.beloyal.registration.dto.customerRegistraton.RegisterUserDto;

public interface CustomerRegistrationService {
    void createCustomer(RegisterUserDto dto);
}
