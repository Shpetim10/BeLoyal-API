package com.shabanaj.beloyal.features.customerApis.dto;

public record LoyaltySettingsSummaryDto(
        int minPointsToRedeem,
        Integer maxPointsToRedeem,
        int pointsPerUnitDiscount,
        Integer maxPointsPerTransaction,
        String expiryType,
        Integer monthsToExpire,
        boolean enabled,
        boolean configured
) {
}
