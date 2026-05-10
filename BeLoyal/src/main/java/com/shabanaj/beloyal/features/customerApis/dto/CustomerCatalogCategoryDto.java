package com.shabanaj.beloyal.features.customerApis.dto;

public record CustomerCatalogCategoryDto(
        Long id,
        String name,
        String description,
        int sortOrder
) {
}
