package com.shabanaj.beloyal.features.loyaltyCard.service.impl;

import com.shabanaj.beloyal.common.Exception.LoyaltyCardNotFound;
import com.shabanaj.beloyal.common.Helpers.UserFinder;
import com.shabanaj.beloyal.features.loyaltyCard.component.LoyaltyCodeGenerator;
import com.shabanaj.beloyal.features.loyaltyCard.repository.LoyaltyCardRepository;
import com.shabanaj.beloyal.features.loyaltyCard.service.LoyaltyCardService;
import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.LoyaltyCardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoyaltyCardServiceImpl implements LoyaltyCardService {
    private final LoyaltyCardRepository loyaltyCardRepository;
    private final LoyaltyCodeGenerator loyaltyCodeGenerator;
    private final UserFinder userFinder;
    private final CustomerProfileService customerProfileService;
    private final Clock clock;

    @Override
    @Transactional
    public LoyaltyCard createLoyaltyCard(CustomerProfile customerProfile) {
        // Generate codes
        String qrToken= getQrTokenIdentifier();
        String manualCode= getManualCodeIdentifier();

        // Create Card
        LoyaltyCard loyaltyCard = new LoyaltyCard();
        loyaltyCard.setQrToken(qrToken);
        loyaltyCard.setManualCode(manualCode);
        loyaltyCard.setCustomerProfile(customerProfile);
        loyaltyCard.setIssuedAt(LocalDateTime.now(clock));
        loyaltyCard.setStatus(LoyaltyCardStatus.ACTIVE);

        // Persist
        return loyaltyCardRepository.save(loyaltyCard);
    }

    @Override
    public LoyaltyCard getLoyaltyCardFromQrToken(String qrToken) {
        Optional<LoyaltyCard> loyaltyCard= loyaltyCardRepository.getLoyaltyCardByQrToken(qrToken);

        if(loyaltyCard.isEmpty()){
            throw new LoyaltyCardNotFound("Loyalty card was not found");
        }
        return loyaltyCard.get();
    }

    @Override
    public LoyaltyCard getLoyaltyCardFromManualCode(String manualCode) {
        Optional<LoyaltyCard> loyaltyCard= loyaltyCardRepository.getLoyaltyCardByManualCode(manualCode);

        if(loyaltyCard.isEmpty()){
            throw new LoyaltyCardNotFound("Loyalty card was not found");
        }

        return loyaltyCard.get();
    }

    @Override
    public void changeStatus(Long userId, LoyaltyCardStatus loyaltyCardStatus) {
        // Find user
        User user= userFinder.findByIdOrThrows(userId);
        // Find customer profile
        CustomerProfile customerProfile= customerProfileService.getCustomerProfileByUser(user);
        // Find loyaltyCard
        Optional<LoyaltyCard> loyaltyCard= loyaltyCardRepository.getLoyaltyCardByCustomerProfile(customerProfile);

        if(loyaltyCard.isEmpty()){
            throw new LoyaltyCardNotFound("Loyalty card was not found");
        }

        loyaltyCard.get().setStatus(loyaltyCardStatus);
    }

    @Override
    public LoyaltyCard getLoyaltyCardForUser(User user) {
        CustomerProfile customerProfile=  customerProfileService.getCustomerProfileByUser(user);
        // Find loyaltyCard
        Optional<LoyaltyCard> loyaltyCard= loyaltyCardRepository.getLoyaltyCardByCustomerProfile(customerProfile);

        if(loyaltyCard.isEmpty()){
            throw new LoyaltyCardNotFound("Loyalty card was not found");
        }
        return loyaltyCard.get();
    }

    // Helpers
    private String getQrTokenIdentifier(){
        String qrToken= loyaltyCodeGenerator.generateQrToken();
        Optional<LoyaltyCard> card= loyaltyCardRepository.getLoyaltyCardByQrToken(qrToken);

        if(card.isPresent()){
            return getQrTokenIdentifier();
        }

        return qrToken;
    }

    private String getManualCodeIdentifier(){
        String manualCode= loyaltyCodeGenerator.generateManualCode();
        Optional<LoyaltyCard> card= loyaltyCardRepository.getLoyaltyCardByManualCode(manualCode);

        if(card.isPresent()){
            return getManualCodeIdentifier();
        }

        return manualCode;
    }
}
