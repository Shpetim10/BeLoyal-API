package com.shabanaj.beloyal.Dto.Registration.customerRegistraton;


import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ActivationResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Set<String> roles;
    private boolean emailVerified;
    private boolean profileComplete;
    private boolean alreadyVerified;
}