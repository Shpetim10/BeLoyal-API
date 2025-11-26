package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Dto.RegisterUserDto;

public interface AuthenticationService {
    void registerUser(RegisterUserDto dto);
    void activateUser(String token);
}
