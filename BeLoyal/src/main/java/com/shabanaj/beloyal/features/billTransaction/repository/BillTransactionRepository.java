package com.shabanaj.beloyal.features.billTransaction.repository;

import com.shabanaj.beloyal.model.Entity.BillTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BillTransactionRepository extends JpaRepository<BillTransaction, Long> {
    Optional<BillTransaction> findByBusinessIdAndIdempotencyKey(Long businessId, String idempotencyKey);

    @Modifying
    @Query("DELETE FROM BillTransaction bt WHERE bt.business.id = :businessId")
    void deleteByBusinessId(@Param("businessId") Long businessId);
}
