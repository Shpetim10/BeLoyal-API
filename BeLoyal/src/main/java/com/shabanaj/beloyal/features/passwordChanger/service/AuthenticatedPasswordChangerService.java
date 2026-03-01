package com.shabanaj.beloyal.features.passwordChanger.service;

import com.shabanaj.beloyal.features.passwordChanger.dto.AuthenticatedPasswordChangeRequest;
import com.shabanaj.beloyal.features.passwordChanger.dto.AuthenticatedPasswordChangeResponse;

public interface AuthenticatedPasswordChangerService {
    AuthenticatedPasswordChangeResponse changePassword(Long userId, AuthenticatedPasswordChangeRequest authenticatedPasswordChangeRequest);
}
