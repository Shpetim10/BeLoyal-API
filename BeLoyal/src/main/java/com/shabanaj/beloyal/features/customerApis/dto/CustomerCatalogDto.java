package com.shabanaj.beloyal.features.customerApis.dto;

import java.util.List;

public record CustomerCatalogDto(
        List<CustomerCatalogCategoryDto> categories,
        List<CustomerCatalogItemDto> items,
        List<CustomerCatalogVariantDto> variants
) {
}
