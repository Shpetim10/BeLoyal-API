package com.shabanaj.beloyal.features.customerApis.dto;

public record CustomerDetailsTabDto(
        String about,
        String phone,
        String email,
        String categoryLabel,
        String websiteUrl,
        String customerNotes,
        String termsSummary
) {
}
