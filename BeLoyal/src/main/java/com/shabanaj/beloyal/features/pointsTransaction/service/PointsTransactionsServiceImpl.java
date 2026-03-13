package com.shabanaj.beloyal.features.pointsTransaction.service;

import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointsTransactionsServiceImpl implements PointsTransactionService {
    private final PointsTransactionRepository pointsTransactionRepository;

    @Override
    public PointsTransaction save(PointsTransaction pointsTransaction) {
        return pointsTransactionRepository.save(pointsTransaction);
    }
}
