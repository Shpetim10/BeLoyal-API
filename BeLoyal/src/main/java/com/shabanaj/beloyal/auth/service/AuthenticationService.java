package com.shabanaj.beloyal.auth.service;

import com.shabanaj.beloyal.auth.dto.LogoutRequest;
import com.shabanaj.beloyal.auth.dto.RefreshRequest;
import com.shabanaj.beloyal.auth.dto.LoginResponse;
import com.shabanaj.beloyal.registration.dto.businessRegistration.VerifyOwnershipRequest;
import com.shabanaj.beloyal.registration.dto.businessRegistration.VerifyOwnershipResponse;
import com.shabanaj.beloyal.registration.dto.customerRegistraton.ActivationResponse;

public interface AuthenticationService {
    ActivationResponse activateUser(String token);
    void resendVerificationEmail(String email);
    LoginResponse refresh(RefreshRequest refreshRequest);
    void logOut(LogoutRequest logoutRequest);
    VerifyOwnershipResponse verifyOwnership(VerifyOwnershipRequest verifyOwnershipRequest);
}
