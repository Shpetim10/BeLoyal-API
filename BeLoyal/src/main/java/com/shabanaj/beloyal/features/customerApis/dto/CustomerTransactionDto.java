package com.shabanaj.beloyal.features.customerApis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerTransactionDto(
        Long id,
        Long businessId,
        String businessName,
        String type,
        int points,
        LocalDateTime date,
        String description,
        BigDecimal netAmount,
        BigDecimal billAmount,
        String referenceId
) {
}
