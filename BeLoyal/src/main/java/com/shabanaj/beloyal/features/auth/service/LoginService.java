package com.shabanaj.beloyal.features.auth.service;

import com.shabanaj.beloyal.features.auth.dto.LoginRequest;
import com.shabanaj.beloyal.features.auth.dto.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest);

}
