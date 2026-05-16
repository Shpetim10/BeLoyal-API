package com.shabanaj.beloyal.features.loyaltyCard.repository;

import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoyaltyCardRepository extends JpaRepository<LoyaltyCard, Long> {
    Optional<LoyaltyCard> getLoyaltyCardByQrToken(String qrToken);
    Optional<LoyaltyCard> getLoyaltyCardByManualCode(String manualCode);

    @Query(
            "SELECT lc FROM LoyaltyCard lc INNER JOIN lc.customerProfile cp " +
                    "INNER JOIN cp.user u WHERE u.email = :email"
    )
    Optional<LoyaltyCard> getLoyaltyCardByEmail(@Param("email") String email);

    Optional<LoyaltyCard> getLoyaltyCardByCustomerProfile(CustomerProfile customerProfile);

    @Modifying
    @Query("DELETE FROM LoyaltyCard lc WHERE lc.customerProfile.id = :customerProfileId")
    void deleteByCustomerProfileId(@Param("customerProfileId") Long customerProfileId);
}
