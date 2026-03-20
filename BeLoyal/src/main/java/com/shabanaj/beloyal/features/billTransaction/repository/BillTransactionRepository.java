package com.shabanaj.beloyal.features.billTransaction.repository;

import com.shabanaj.beloyal.model.Entity.BillTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillTransactionRepository extends JpaRepository<BillTransaction, Long> {
    Optional<BillTransaction> findByBusinessIdAndIdempotencyKey(Long businessId, String idempotencyKey);
}
