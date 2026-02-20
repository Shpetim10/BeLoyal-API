package com.shabanaj.beloyal.user.service;

import com.shabanaj.beloyal.registration.dto.customerRegistraton.ActivationResponse;

public interface UserActivationService {
    ActivationResponse activateUser(String token);
}
