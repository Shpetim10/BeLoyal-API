package com.shabanaj.beloyal.features.registerLoyaltyPoints.service.impl;

import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.PointsCalculatorService;
import com.shabanaj.beloyal.model.Entity.EarningSettings;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PointsCalculatorServiceImpl implements PointsCalculatorService {
    @Override
    public Integer calculatePoints(BigDecimal billAmount, EarningSettings earningSettings) {
        if (billAmount == null || earningSettings == null
                || !earningSettings.isEnabled() || !earningSettings.isConfigured()
                || earningSettings.getAmountPer() == null
                || earningSettings.getAmountPer().compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        return billAmount
                .divide(earningSettings.getAmountPer(), 0, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(earningSettings.getPointsPer()))
                .intValue();
    }

    @Override
    public Integer applyCaps(Integer points, LoyaltySettings loyaltySettings) {
        // check for null
        if (points == null) return 0;
        if (loyaltySettings == null || loyaltySettings.getMaxPointsPerTransactions() == null) {
            return points;
        }

        return Math.min(points, loyaltySettings.getMaxPointsPerTransactions());
    }
}
