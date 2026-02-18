package com.shabanaj.beloyal.common.Configurations;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemSettings {
    private final int MAX_LOGIN_ATTEMPTS=5;
    private final int LOCK_MINUTES=60;

    public int getMAX_LOGIN_ATTEMPTS() {
        return MAX_LOGIN_ATTEMPTS;
    }

    public int getLOCK_MINUTES() {
        return LOCK_MINUTES;
    }
}
