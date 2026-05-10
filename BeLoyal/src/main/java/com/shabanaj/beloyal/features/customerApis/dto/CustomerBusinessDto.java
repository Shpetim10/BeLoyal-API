package com.shabanaj.beloyal.features.customerApis.dto;

import java.util.List;

public record CustomerBusinessDto(
        Long id,
        String name,
        int categoryId,
        String categoryKey,
        String categoryLabel,
        int points,
        int nextRewardPoints,
        Boolean isOpen,
        Double rating,
        boolean hasLogo,
        String logoUrl,
        String brandColorHex,
        List<String> gradientHex,
        String address,
        String phone,
        String email,
        String description,
        boolean hasOffer,
        String offerLabel
) {
}
