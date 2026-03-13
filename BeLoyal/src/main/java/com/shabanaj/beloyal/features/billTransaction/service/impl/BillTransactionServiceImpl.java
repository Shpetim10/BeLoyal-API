package com.shabanaj.beloyal.features.billTransaction.service.impl;

import com.shabanaj.beloyal.features.billTransaction.repository.BillTransactionRepository;
import com.shabanaj.beloyal.features.billTransaction.service.BillTransactionService;
import com.shabanaj.beloyal.model.Entity.BillTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillTransactionServiceImpl implements BillTransactionService {
    private final BillTransactionRepository billTransactionRepository;

    @Override
    public BillTransaction save(BillTransaction billTransaction) {
        return billTransactionRepository.save(billTransaction);
    }
}
