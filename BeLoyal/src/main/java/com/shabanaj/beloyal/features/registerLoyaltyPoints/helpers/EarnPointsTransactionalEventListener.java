package com.shabanaj.beloyal.features.registerLoyaltyPoints.helpers;

import com.shabanaj.beloyal.common.redis.idempotency.EarnPointsIdempotencyService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsCommittedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EarnPointsTransactionalEventListener {
    private final EarnPointsIdempotencyService idempotencyService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEarnPointsCommitted(EarnPointsCommittedEvent event) {
        idempotencyService.markCompleted(
                event.getBusinessId(),
                event.getIdempotencyKey(),
                event.getRequestHash(),
                event.getResponse()
        );
    }
}
