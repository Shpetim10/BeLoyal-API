package com.shabanaj.beloyal.features.pointsTransaction.repository;

import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, Integer> {
    List<PointsTransaction> findAllByBillTransactionId(Long billTransactionId);
}
