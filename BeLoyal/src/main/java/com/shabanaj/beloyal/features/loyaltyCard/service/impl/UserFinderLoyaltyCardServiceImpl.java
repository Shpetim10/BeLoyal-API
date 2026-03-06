package com.shabanaj.beloyal.features.loyaltyCard.service.impl;

import com.shabanaj.beloyal.features.loyaltyCard.service.LoyaltyCardService;
import com.shabanaj.beloyal.features.loyaltyCard.service.UserFinderLoyaltyCardService;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import com.shabanaj.beloyal.model.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFinderLoyaltyCardServiceImpl implements UserFinderLoyaltyCardService {
    private final LoyaltyCardService loyaltyCardService;

    @Override
    public User getUserFromQrToken(String qrToken) {
        LoyaltyCard loyaltyCard= loyaltyCardService.getLoyaltyCardFromQrToken(qrToken);

        CustomerProfile customerProfile= loyaltyCard.getCustomerProfile();

        return customerProfile.getUser();
    }

    @Override
    public User getUserFromManualCode(String manualCode) {
        LoyaltyCard loyaltyCard= loyaltyCardService.getLoyaltyCardFromManualCode(manualCode);

        CustomerProfile customerProfile= loyaltyCard.getCustomerProfile();

        return customerProfile.getUser();
    }
}
