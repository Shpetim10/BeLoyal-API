package com.shabanaj.beloyal.features.pointsBucketConsumption.repository;

import com.shabanaj.beloyal.model.Entity.PointsBucketConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointsBucketConsumptionRepository extends JpaRepository<PointsBucketConsumption, Long> {

    @Modifying
    @Query("DELETE FROM PointsBucketConsumption pbc WHERE pbc.pointsBucket.loyaltyAccount.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);

    @Modifying
    @Query("DELETE FROM PointsBucketConsumption pbc WHERE pbc.pointsBucket.loyaltyAccount.customerProfile.id = :customerProfileId")
    void deleteByCustomerProfileId(@Param("customerProfileId") Long customerProfileId);
}
