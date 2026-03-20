package com.shabanaj.beloyal.features.registerLoyaltyPoints.service;

import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsTransactionRequest;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsTransactionResponse;

public interface EarnPointsTransactionService {
    EarnPointsTransactionResponse earnPoints(Long businessId, Long userId, String idempotencyKey, EarnPointsTransactionRequest request);
}
