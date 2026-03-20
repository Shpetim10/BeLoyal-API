package com.shabanaj.beloyal.features.registerLoyaltyPoints.dto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EarnPointsCommittedEvent extends ApplicationEvent {
    private final Long businessId;
    private final String idempotencyKey;
    private final String requestHash;
    private final EarnPointsTransactionResponse response;

    public EarnPointsCommittedEvent(Object source, Long businessId, String idempotencyKey, String requestHash, EarnPointsTransactionResponse response) {
        super(source);
        this.businessId = businessId;
        this.idempotencyKey = idempotencyKey;
        this.requestHash = requestHash;
        this.response = response;
    }
}
