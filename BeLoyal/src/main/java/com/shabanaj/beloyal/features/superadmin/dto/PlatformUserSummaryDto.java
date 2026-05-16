package com.shabanaj.beloyal.features.superadmin.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record PlatformUserSummaryDto(
        Long id,
        String firstName,
        String lastName,
        String username,
        String email,
        String phoneNumber,
        Set<String> roles,
        String status,
        boolean emailVerified,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        List<BusinessMembershipSummaryDto> businessMemberships,
        LoyaltySummaryDto loyaltySummary
) {}
