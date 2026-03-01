package com.shabanaj.beloyal.features.auth.service;

import com.shabanaj.beloyal.features.auth.dto.LogoutRequest;
import com.shabanaj.beloyal.features.auth.dto.RefreshRequest;
import com.shabanaj.beloyal.features.auth.dto.LoginResponse;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.VerifyOwnershipRequest;
import com.shabanaj.beloyal.features.registration.dto.businessRegistration.VerifyOwnershipResponse;
import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.ActivationResponse;

public interface AuthenticationService {
    ActivationResponse activateUser(String token);
    void resendVerificationEmail(String email);
    LoginResponse refresh(RefreshRequest refreshRequest);
    void logOut(LogoutRequest logoutRequest);
    VerifyOwnershipResponse verifyOwnership(VerifyOwnershipRequest verifyOwnershipRequest);
}
