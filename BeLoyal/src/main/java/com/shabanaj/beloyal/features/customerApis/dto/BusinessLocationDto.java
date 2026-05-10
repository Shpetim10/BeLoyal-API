package com.shabanaj.beloyal.features.customerApis.dto;

public record BusinessLocationDto(
        String addressLine1,
        String addressLine2,
        String city,
        String country,
        String postalCode,
        Double latitude,
        Double longitude,
        String mapLabel
) {
}
