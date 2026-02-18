package com.shabanaj.beloyal.Dto.Registration.businessRegistration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RejectBusinessApplicationRequest {
    @NotBlank
    @Size(max=2000)
    public String reason;
}
