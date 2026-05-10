package com.shabanaj.beloyal.features.customerApis.dto;

import java.util.List;

public record CustomerHomeResponse(
        CustomerSummaryDto summary,
        List<CustomerCategoryDto> categories,
        List<CustomerBusinessDto> businesses,
        List<CustomerPromotionDto> promotions,
        List<CustomerTransactionDto> transactions
) {
}
