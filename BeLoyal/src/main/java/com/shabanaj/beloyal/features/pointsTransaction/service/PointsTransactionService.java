package com.shabanaj.beloyal.features.pointsTransaction.service;


import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import com.shabanaj.beloyal.features.pointsTransaction.dto.PointTransactionViewDto;

public interface PointsTransactionService {
    PointsTransaction save(PointsTransaction pointsTransaction);
    PointTransactionViewDto getById(Long id);
}
