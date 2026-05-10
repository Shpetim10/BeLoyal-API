package com.shabanaj.beloyal.features.loyaltySettings.repository;

import com.shabanaj.beloyal.model.Entity.LoyaltySettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LoyaltySettingsRepository extends JpaRepository<LoyaltySettings, Long> {
    Optional<LoyaltySettings> findByBusinessId(Long businessId);
    List<LoyaltySettings> findAllByBusinessIdIn(Collection<Long> businessIds);
}
