package com.shabanaj.beloyal.user.service;

import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.registration.dto.RegisterUserDto;

public interface UserRegistrationBuilderService {
    User buildUserFromRegistration(RegisterUserDto dto);
}
