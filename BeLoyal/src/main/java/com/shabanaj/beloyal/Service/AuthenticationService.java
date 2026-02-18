package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Dto.Auth.LogoutRequest;
import com.shabanaj.beloyal.Dto.Auth.RefreshRequest;
import com.shabanaj.beloyal.Dto.Login.LoginRequest;
import com.shabanaj.beloyal.Dto.Login.LoginResponse;
import com.shabanaj.beloyal.Dto.Registration.businessRegistration.VerifyOwnershipRequest;
import com.shabanaj.beloyal.Dto.Registration.businessRegistration.VerifyOwnershipResponse;
import com.shabanaj.beloyal.Dto.Registration.customerRegistraton.ActivationResponse;
import com.shabanaj.beloyal.Dto.Registration.customerRegistraton.RegisterUserDto;

public interface AuthenticationService {
    void registerCustomer(RegisterUserDto dto);
    ActivationResponse activateUser(String token);
    LoginResponse loginUser(LoginRequest request);
    void resendVerificationEmail(String email);
    LoginResponse refresh(RefreshRequest refreshRequest);
    void logOut(LogoutRequest logoutRequest);
    VerifyOwnershipResponse verifyOwnership(VerifyOwnershipRequest verifyOwnershipRequest);
}
