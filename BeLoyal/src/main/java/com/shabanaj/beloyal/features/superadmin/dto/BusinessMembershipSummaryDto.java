package com.shabanaj.beloyal.features.superadmin.dto;

public record BusinessMembershipSummaryDto(
        Long businessId,
        String businessName,
        String role,
        String memberStatus
) {}
