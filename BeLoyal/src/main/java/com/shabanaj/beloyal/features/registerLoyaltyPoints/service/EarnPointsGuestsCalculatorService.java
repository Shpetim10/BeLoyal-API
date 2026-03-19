package com.shabanaj.beloyal.features.registerLoyaltyPoints.service;

import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnComputationResult;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.GuestPointsResult;
import com.shabanaj.beloyal.model.Entity.*;

import java.math.BigDecimal;
import java.util.List;

public interface EarnPointsGuestsCalculatorService {
    EarnComputationResult computeEarnPreview(
            Business business,
            BigDecimal billAmount,
            List<Long> guestIds,
            Long primaryGuestId,
            EarningSettings earningSettings,
            LoyaltySettings loyaltySettings
    );

    List<GuestPointsResult> distributePoints(Business business, List<Long> guestIds, int equalShare, int remainder);
    void distributeAndPersistPointsTransactionsAndPointsBuckets(
            Business business,
            BusinessMember businessMember,
            BillTransaction billTransaction,
            List<GuestPointsResult> guestResults,
            EarningSettings earningSettings,
            LoyaltySettings loyaltySettings);
}
