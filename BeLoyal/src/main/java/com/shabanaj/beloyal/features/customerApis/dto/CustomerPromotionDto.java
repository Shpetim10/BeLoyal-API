package com.shabanaj.beloyal.features.customerApis.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        String expiresIn,
        List<String> gradientHex,
        boolean isHot,
        boolean isUsed,
        int usageCount,
        Integer usageLimit,
        String termsAndConditions,
        boolean isOwned,
        String qrCode,
        int customerRedemptionCount
) {
}
