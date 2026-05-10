package com.shabanaj.beloyal.features.loyaltyCard.dto;

public record CustomerPointsSummary(
        Integer currentPoints,
        Integer lifetimePoints,
        Integer lifetimeSpentPoints,
        Integer businessesVisited
) {
}
