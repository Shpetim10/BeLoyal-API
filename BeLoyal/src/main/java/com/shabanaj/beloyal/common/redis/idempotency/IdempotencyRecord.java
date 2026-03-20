package com.shabanaj.beloyal.common.redis.idempotency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord implements Serializable {
    private String status; // PROCESSING or COMPLETED
    private String requestHash;
    private String responseJson;
}
