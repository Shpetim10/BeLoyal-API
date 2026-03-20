package com.shabanaj.beloyal.common.redis.idempotency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shabanaj.beloyal.common.Exception.IdempotencyAlreadyProcessingException;
import com.shabanaj.beloyal.common.Exception.IdempotencyConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EarnPointsIdempotencyService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "idem:earn:";
    private static final Duration PROCESSING_TTL = Duration.ofMinutes(2);
    private static final Duration COMPLETED_TTL = Duration.ofDays(3);

    private String buildKey(Long businessId, String idempotencyKey) {
        return KEY_PREFIX + businessId + ":" + idempotencyKey;
    }

    /**
     * Checks if a request is already being processed or has been completed.
     * If absent, creates a PROCESSING record and returns null (indicating safe to proceed).
     * If COMPLETED, deserializes and returns the cached response.
     * If PROCESSING, throws IdempotencyAlreadyProcessingException.
     * If requestHash mismatches, throws IdempotencyConflictException.
     *
     * @return Cached response (json string) if COMPLETED, null if safe to proceed
     */
    public String beginOrReplay(Long businessId, String idempotencyKey, String requestHash) {
        String key = buildKey(businessId, idempotencyKey);
        
        IdempotencyRecord newRecord = IdempotencyRecord.builder()
                .status("PROCESSING")
                .requestHash(requestHash)
                .build();

        Boolean isSet = redisTemplate.opsForValue().setIfAbsent(key, newRecord, PROCESSING_TTL);

        if (Boolean.TRUE.equals(isSet)) {
            // Safe to process.
            return null;
        }

        // Key already exists
        Object cachedObj = redisTemplate.opsForValue().get(key);
        if (cachedObj == null) {
            // Just expired
            redisTemplate.opsForValue().set(key, newRecord, PROCESSING_TTL);
            return null;
        }

        IdempotencyRecord cachedRecord = objectMapper.convertValue(cachedObj, IdempotencyRecord.class);

        // Verify payload matches
        if (!cachedRecord.getRequestHash().equals(requestHash)) {
            throw new IdempotencyConflictException("Idempotency key already used for a different request payload.");
        }

        if ("PROCESSING".equals(cachedRecord.getStatus())) {
            throw new IdempotencyAlreadyProcessingException("Request is already being processed.");
        }

        if ("COMPLETED".equals(cachedRecord.getStatus())) {
            return cachedRecord.getResponseJson();
        }

        return null;
    }

    public void markCompleted(Long businessId, String idempotencyKey, String requestHash, Object responseObject) {
        String key = buildKey(businessId, idempotencyKey);
        
        try {
            String responseJson = objectMapper.writeValueAsString(responseObject);
            IdempotencyRecord completedRecord = IdempotencyRecord.builder()
                    .status("COMPLETED")
                    .requestHash(requestHash)
                    .responseJson(responseJson)
                    .build();

            redisTemplate.opsForValue().set(key, completedRecord, COMPLETED_TTL);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize response for idempotency caching", e);
        }
    }

    public void clearProcessing(Long businessId, String idempotencyKey) {
        String key = buildKey(businessId, idempotencyKey);
        
        Object cachedObj = redisTemplate.opsForValue().get(key);
        if (cachedObj != null) {
            IdempotencyRecord cachedRecord = objectMapper.convertValue(cachedObj, IdempotencyRecord.class);
            // Only clear if it hasn't somehow become COMPLETED already
            if ("PROCESSING".equals(cachedRecord.getStatus())) {
                redisTemplate.delete(key);
            }
        }
    }
}
