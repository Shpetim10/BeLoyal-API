package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Service.EmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendActivationEmail(User user, String token) {

    }
}
