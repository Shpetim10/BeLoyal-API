package com.shabanaj.beloyal.features.customerApis.dto;

import java.math.BigDecimal;

public record CustomerCatalogVariantDto(
        Long id,
        Long itemId,
        String name,
        String description,
        BigDecimal price,
        String currency,
        boolean isDefault,
        boolean isAvailable,
        Integer earnedPoints
) {
}
