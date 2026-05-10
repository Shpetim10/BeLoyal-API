package com.shabanaj.beloyal.features.customerApis.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CustomerPromotionDto(
        Long id,
        Long businessId,
        Long couponId,
        String businessName,
        String title,
        String description,
        String promotionType,
        String status,
        String discountDisplay,
        int pointCost,
        LocalDateTime expiresAt,
        List<String> gradientHex,
        boolean isHot,
        boolean isUsed,
        int usageCount,
        Integer usageLimit,
        String termsAndConditions,
        boolean isOwned
) {
}
