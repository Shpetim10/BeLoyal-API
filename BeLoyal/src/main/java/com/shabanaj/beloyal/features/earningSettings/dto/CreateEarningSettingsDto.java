package com.shabanaj.beloyal.features.earningSettings.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateEarningSettingsDto {
    @NotNull
    @Min(0)
    private Integer pointsPer;

    @NotNull
    @Min(1)
    private BigDecimal amountPer;
}
