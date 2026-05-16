package com.shabanaj.beloyal.features.earningSettings.repository;

import com.shabanaj.beloyal.model.Entity.EarningSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EarningSettingsRepository extends JpaRepository<EarningSettings, Integer> {
    Optional<EarningSettings> findByBusinessId(Long businessId);

    @Modifying
    @Query("DELETE FROM EarningSettings es WHERE es.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);
}
