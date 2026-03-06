package com.shabanaj.beloyal.features.loyaltyCard.service;

import com.shabanaj.beloyal.model.Entity.User;

public interface UserFinderLoyaltyCardService {
    User getUserFromQrToken(String qrToken);
    User getUserFromManualCode(String manualCode);
}
