package com.shabanaj.beloyal.user.service;

import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.registration.dto.customerRegistraton.RegisterUserDto;

public interface UserRegistrationBuilderService {
    User buildUserFromRegistration(RegisterUserDto dto);
}
