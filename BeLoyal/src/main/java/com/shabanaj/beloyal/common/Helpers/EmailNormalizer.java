package com.shabanaj.beloyal.common.Helpers;

import org.springframework.stereotype.Component;

@Component
public class EmailNormalizer {
    public String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
