package com.shabanaj.beloyal.passwordChanger.service;

import com.shabanaj.beloyal.passwordChanger.dto.AuthenticatedPasswordChangeRequest;
import com.shabanaj.beloyal.passwordChanger.dto.AuthenticatedPasswordChangeResponse;

public interface AuthenticatedPasswordChangerService {
    AuthenticatedPasswordChangeResponse changePassword(Long userId, AuthenticatedPasswordChangeRequest authenticatedPasswordChangeRequest);
}
