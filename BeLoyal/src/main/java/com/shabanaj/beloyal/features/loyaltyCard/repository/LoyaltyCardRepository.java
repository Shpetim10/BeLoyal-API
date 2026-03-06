package com.shabanaj.beloyal.features.loyaltyCard.repository;

import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoyaltyCardRepository extends JpaRepository<LoyaltyCard, Long> {
    Optional<LoyaltyCard> getLoyaltyCardByQrToken(String qrToken);
    Optional<LoyaltyCard> getLoyaltyCardByManualCode(String manualCode);
    Optional<LoyaltyCard> getLoyaltyCardByCustomerProfile(CustomerProfile customerProfile);
}
