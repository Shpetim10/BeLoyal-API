package com.shabanaj.beloyal.email.service;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.User;

public interface EmailService {
    void sendActivationEmail(User user, String token);
    void sendBusinessRegistrationEmail(User user, Business business);
}
