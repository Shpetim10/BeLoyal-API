package com.shabanaj.beloyal.features.coupon.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReviveCouponRequest(
        @NotNull @FutureOrPresent LocalDateTime startDate,
        @NotNull @FutureOrPresent LocalDateTime endDate
) {
}
