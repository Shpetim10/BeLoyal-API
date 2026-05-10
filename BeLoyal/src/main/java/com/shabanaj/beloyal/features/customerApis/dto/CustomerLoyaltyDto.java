package com.shabanaj.beloyal.features.customerApis.dto;

import java.time.LocalDateTime;

public record CustomerLoyaltyDto(
        int currentPoints,
        int nextRewardPoints,
        int pointsToNextReward,
        String memberCode,
        String loyaltyPolicy,
        LoyaltySettingsSummaryDto loyaltySettings,
        EarningSettingsSummaryDto earningSettings,
        int lifetimeEarned,
        int lifetimeRedeemed,
        int lifetimeExpired,
        LocalDateTime lastActivityAt
) {
}
