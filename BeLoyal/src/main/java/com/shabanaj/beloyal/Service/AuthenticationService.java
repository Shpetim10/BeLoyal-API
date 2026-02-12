package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Dto.Login.LoginRequest;
import com.shabanaj.beloyal.Dto.Login.LoginResponse;
import com.shabanaj.beloyal.Dto.Registration.RegisterUserDto;

public interface AuthenticationService {
    void registerCustomer(RegisterUserDto dto);
    void activateUser(String token);
    LoginResponse loginUser(LoginRequest request);
}
