package com.shabanaj.beloyal.features.customerApis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerBusinessTransactionDto(
        Long id,
        Long businessId,
        String businessName,
        String type,
        int points,
        LocalDateTime date,
        String description,
        BigDecimal netAmount,
        BigDecimal billAmount,
        BigDecimal discountAmount,
        String referenceId,

        // PointsTransaction context
        String reason,
        String scanMethod,
        BigDecimal moneyAmount,
        BigDecimal ruleAmountPer,
        Integer rulePointsPer,

        // BillTransaction context
        String invoiceReference,
        String note
) {
}
