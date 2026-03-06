package com.shabanaj.beloyal.features.loyaltyCard.service;

import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.LoyaltyCardStatus;

public interface LoyaltyCardService {
    LoyaltyCard createLoyaltyCard(CustomerProfile customerProfile);
    LoyaltyCard getLoyaltyCardFromQrToken(String qrToken);
    LoyaltyCard getLoyaltyCardFromManualCode(String manualCode);
    void changeStatus(Long userId, LoyaltyCardStatus loyaltyCardStatus);
    LoyaltyCard getLoyaltyCardForUser(User user);
}
