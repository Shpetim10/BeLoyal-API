package com.shabanaj.beloyal.features.registerLoyaltyPoints.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EarnPointsTransactionRequest {
    @NotNull
    @DecimalMin(value = "1.0", inclusive = true)
    private BigDecimal billAmount;
    private String invoiceNumber;
    private String note;
    @NotEmpty
    private List<Guest> guests;

    public List<Long> getGuestIds(){
        List<Long> ids= new ArrayList<>();
        for(Guest guest : guests){
            ids.add(guest.customerId);
        }

        return ids;
    }

    @Getter
    @Setter
    public static class Guest{
        @NotNull
        private Long customerId;
    }
}
