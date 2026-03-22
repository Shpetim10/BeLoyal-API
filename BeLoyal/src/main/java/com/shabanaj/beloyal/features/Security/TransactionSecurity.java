package com.shabanaj.beloyal.features.Security;

import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("transactionSecurity")
@RequiredArgsConstructor
public class TransactionSecurity {
    private final PointsTransactionRepository pointsTransactionRepository;

    public boolean canViewTransaction(Long transactionId, Authentication authentication){
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();

        return pointsTransactionRepository.hasAccess(transactionId, userId);
    }
}
