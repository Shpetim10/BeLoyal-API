package com.shabanaj.beloyal.features.customerApis.dto;

public record CustomerSummaryDto(
        int currentPoints,
        int lifetimePoints,
        int spentPoints,
        int businessesVisited,
        int activeCoupons,
        int activeRewards,
        String memberSinceLabel,
        String memberCode
) {
}
