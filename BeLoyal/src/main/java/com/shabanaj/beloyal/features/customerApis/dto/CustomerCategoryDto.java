package com.shabanaj.beloyal.features.customerApis.dto;

public record CustomerCategoryDto(
        int id,
        String key,
        String label,
        int businessCount,
        int sortOrder
) {
}
