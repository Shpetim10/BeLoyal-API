package com.shabanaj.beloyal.Dto.Registration.businessRegistration;

import com.shabanaj.beloyal.Enums.BusinessStatus;

public record SubmitBusinessApplicationResponse(
        Long businessId,
        BusinessStatus status,
        String message
) {
}
