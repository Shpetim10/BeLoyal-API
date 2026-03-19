package com.shabanaj.beloyal.features.earningSettings.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EarningSettingsDto {
    Integer pointsPer;
    BigDecimal amountPer;
}
