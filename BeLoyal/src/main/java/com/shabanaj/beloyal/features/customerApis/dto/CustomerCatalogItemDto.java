package com.shabanaj.beloyal.features.customerApis.dto;

import java.math.BigDecimal;

public record CustomerCatalogItemDto(
        Long id,
        Long categoryId,
        String name,
        String description,
        String imageUrl,
        boolean isAvailable,
        int sortOrder,
        String pointsLabel,
        BigDecimal basePrice,
        String currency,
        String unit
) {
}
