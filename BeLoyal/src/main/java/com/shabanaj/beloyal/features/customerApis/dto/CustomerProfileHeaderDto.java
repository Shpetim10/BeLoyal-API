package com.shabanaj.beloyal.features.customerApis.dto;

import com.shabanaj.beloyal.model.Enums.Gender;

import java.time.LocalDate;

public record CustomerProfileHeaderDto(
        String firstName,
        String lastName,
        String fullName,
        String username,
        String email,
        String phoneNumber,
        String profileImageUrl,
        String avatarInitials,
        String status,
        String memberSince,
        String memberCode,
        LocalDate birthDate,
        Gender gender,
        String city,
        String country,
        String referralCode,
        String referredBy,
        boolean notificationEnabled,
        boolean acceptedTerms
) {
}
