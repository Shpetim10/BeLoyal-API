package com.shabanaj.beloyal.features.loyaltyCard.dto;

import com.shabanaj.beloyal.model.Enums.LoyaltyCardStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeLoyaltyCardStatus {
    private LoyaltyCardStatus loyaltyCardStatus;
}
