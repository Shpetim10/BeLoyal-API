package com.shabanaj.beloyal.features.customerApis.dto;

public record CustomerProfileDetailsResponse(
        CustomerProfileHeaderDto profile,
        CustomerSummaryDto stats
) {
}
