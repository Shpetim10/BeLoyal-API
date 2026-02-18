package com.shabanaj.beloyal.auth.service;

import com.shabanaj.beloyal.model.Entity.User;

public interface AuthenticationAttemptService {
    void authenticateOrThrow(User user, String email, String password);
}
