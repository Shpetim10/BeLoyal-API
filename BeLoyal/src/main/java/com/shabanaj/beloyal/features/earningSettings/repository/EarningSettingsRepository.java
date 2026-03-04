package com.shabanaj.beloyal.features.earningSettings.repository;

import com.shabanaj.beloyal.model.Entity.EarningSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EarningSettingsRepository extends JpaRepository<EarningSettings, Integer> {
    Optional<EarningSettings> findByBusinessId(Long businessId);
}
