package com.shabanaj.beloyal.features.customerApis.dto;

public record CustomerStatsDto(
        int currentPoints,
        String memberSince,
        int lifetimePoints,
        int spentPoints,
        int businessesVisited,
        int activeCoupons,
        String memberCode
) {
}
