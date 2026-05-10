package com.shabanaj.beloyal.features.customerApis.dto;

import java.math.BigDecimal;

public record EarningSettingsSummaryDto(
        int pointsPer,
        BigDecimal amountPer,
        boolean enabled,
        boolean configured
) {
}
