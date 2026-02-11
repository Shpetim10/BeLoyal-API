package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Entity.User;

public interface EmailService {
    void sendActivationEmail(User user, String token);
}
