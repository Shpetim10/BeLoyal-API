package com.shabanaj.beloyal.features.customerLookup.service.impl;

import com.shabanaj.beloyal.common.Exception.LoyaltyCardNotFound;
import com.shabanaj.beloyal.features.customerLookup.service.CustomerProfileLookupService;
import com.shabanaj.beloyal.features.loyaltyCard.repository.LoyaltyCardRepository;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("qrTokenLookup")
@RequiredArgsConstructor
public class CustomerProfileLookupByQrTokenService implements CustomerProfileLookupService {
    private final LoyaltyCardRepository loyaltyCardRepository;

    @Override
    public CustomerProfile search(String query) {
        Optional<LoyaltyCard> loyaltyCard= loyaltyCardRepository.getLoyaltyCardByQrToken(query);

        if(loyaltyCard.isEmpty()){
            throw new LoyaltyCardNotFound("This loyalty card is not found");
        }

        return loyaltyCard.get().getCustomerProfile();
    }
}
