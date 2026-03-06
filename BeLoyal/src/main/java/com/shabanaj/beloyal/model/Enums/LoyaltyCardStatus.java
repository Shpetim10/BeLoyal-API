package com.shabanaj.beloyal.model.Enums;

import lombok.Getter;

@Getter
public enum LoyaltyCardStatus {
    ACTIVE(1, "Active", "The card/code can be scanned and used normally."),
    SUSPENDED(2, "Suspended", "Temporarily unusable. Good for fraud suspicion, customer request, or admin hold."),
    REVOKED(3, "Revoked", "Permanently invalid. The card should never work again."),
    REPLACED(4,"Replaced", "This card is replaced by the system.");

    private int id;
    private String name;
    private String description;

    LoyaltyCardStatus(int id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
