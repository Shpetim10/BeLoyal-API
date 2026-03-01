package com.shabanaj.beloyal.features.user.service;

import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.ActivationResponse;

public interface UserActivationService {
    ActivationResponse activateUser(String token);
}
