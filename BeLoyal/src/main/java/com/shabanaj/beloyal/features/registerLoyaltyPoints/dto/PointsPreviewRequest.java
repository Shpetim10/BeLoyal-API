package com.shabanaj.beloyal.features.registerLoyaltyPoints.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PointsPreviewRequest {
    @NotNull
    @Min(1)
    private BigDecimal billAmount;

    private String couponQrCode;

    @NotEmpty
    private List<Guest> guests;

    public List<Long> getGuestIds() {
        List<Long> guestIds = new ArrayList<>();
        for (Guest guest : guests) {
            guestIds.add(guest.getCustomerId());
        }

        return guestIds;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Guest {
        private Long customerId;
    }
}
