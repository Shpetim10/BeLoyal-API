package com.shabanaj.beloyal.Dto.Registration;


import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ActivationResponse {
    private String message;
    private String token;
    private String tokenType;
    private Set<String> roles;
    private boolean emailVerified;
    private boolean profileComplete;
    private boolean alreadyVerified;
}