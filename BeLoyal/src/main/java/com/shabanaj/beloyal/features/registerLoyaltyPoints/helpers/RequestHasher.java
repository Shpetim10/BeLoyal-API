package com.shabanaj.beloyal.features.registerLoyaltyPoints.helpers;

import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsTransactionRequest;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

public class RequestHasher {
    
    public static String hashEarnPointsRequest(Long businessId, EarnPointsTransactionRequest request) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            StringBuilder sb = new StringBuilder();
            sb.append(businessId).append("|");
            
            // Note: we can format bigdecimal strictly to standardize the hash
            BigDecimal amount = request.getBillAmount() != null ? request.getBillAmount().stripTrailingZeros() : BigDecimal.ZERO;
            sb.append(amount.toPlainString()).append("|");
            
            if (request.getGuests() != null) {
                String guestIds = request.getGuests().stream()
                        .map(g -> g.getCustomerId().toString())
                        .sorted() // sort to ensure consistent hash if list identical elements in different order
                        .collect(Collectors.joining(","));
                sb.append(guestIds).append("|");
            }
            
            byte[] encodedhash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to instantiate SHA-256 MessageDigest", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
