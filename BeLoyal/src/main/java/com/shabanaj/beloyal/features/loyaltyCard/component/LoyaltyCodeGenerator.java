package com.shabanaj.beloyal.features.loyaltyCard.component;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class LoyaltyCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final char[] MANUAL_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    public String generateQrToken() {
        byte[] bytes = new byte[16]; // 128-bit random
        RANDOM.nextBytes(bytes);

        // URL-safe, no padding
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    public String generateManualCode() {
        return randomGroup(4) + "-" + randomGroup(4);
    }

    private String randomGroup(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(MANUAL_ALPHABET[RANDOM.nextInt(MANUAL_ALPHABET.length)]);
        }
        return sb.toString();
    }
}