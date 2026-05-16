package com.shabanaj.beloyal.features.superadmin.dto;

public record LoyaltySummaryDto(
        long totalEarned,
        long totalSpent,
        long totalExpired,
        long availablePoints,
        long businessCount
) {}
