package com.shabanaj.beloyal.features.registration.dto.businessRegistration;

import com.shabanaj.beloyal.model.Enums.BusinessStatus;

public record SubmitBusinessApplicationResponse(
        Long businessId,
        BusinessStatus status,
        String message
) {
}
