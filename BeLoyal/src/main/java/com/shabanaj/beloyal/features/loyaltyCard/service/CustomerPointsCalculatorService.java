package com.shabanaj.beloyal.features.loyaltyCard.service;

import com.shabanaj.beloyal.features.loyaltyCard.dto.CustomerPointsSummary;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;

public interface CustomerPointsCalculatorService {
    Integer getCustomersCurrentPoints(CustomerProfile customerProfile);
    Integer getLifetimePoints(CustomerProfile customerProfile);
    Integer getLifetimeSpentPoints(CustomerProfile customerProfile);
    CustomerPointsSummary getCustomerPointsSummary(CustomerProfile customerProfile);
}
