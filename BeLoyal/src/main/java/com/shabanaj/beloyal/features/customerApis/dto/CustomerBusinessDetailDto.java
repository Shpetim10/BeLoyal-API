package com.shabanaj.beloyal.features.customerApis.dto;

public record CustomerBusinessDetailDto(
        Long id,
        String name,
        int categoryId,
        String categoryKey,
        String categoryLabel,
        Double rating,
        boolean hasLogo,
        String logoUrl,
        String description,
        String address,
        String phone,
        String email,
        String websiteUrl,
        BusinessLocationDto location
) {
}
