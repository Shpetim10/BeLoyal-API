package com.shabanaj.beloyal.features.registerLoyaltyPoints.service;

import com.shabanaj.beloyal.model.Entity.EarningSettings;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;

import java.math.BigDecimal;

public interface PointsCalculatorService {
    Integer calculatePoints(BigDecimal billAmount, EarningSettings earningSettings);
    Integer applyCaps(Integer points, LoyaltySettings loyaltySettings);
}
