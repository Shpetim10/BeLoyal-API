package com.shabanaj.beloyal.auth.service;

import com.shabanaj.beloyal.auth.dto.LoginRequest;
import com.shabanaj.beloyal.auth.dto.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest);

}
