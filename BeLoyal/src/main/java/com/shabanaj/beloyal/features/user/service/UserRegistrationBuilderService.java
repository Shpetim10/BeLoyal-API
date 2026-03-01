package com.shabanaj.beloyal.features.user.service;

import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.features.registration.dto.RegisterUserDto;

public interface UserRegistrationBuilderService {
    User buildUserFromRegistration(RegisterUserDto dto);
}
