package com.shabanaj.beloyal.common.Configurations;

import com.shabanaj.beloyal.common.Exception.ApiException;
import com.shabanaj.beloyal.common.Exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Optional: one consistent response shape
    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message, HttpServletRequest req) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("message", message);
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    // Handle @Valid DTO validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest req) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Validation failed");

        return error(HttpStatus.BAD_REQUEST, errorMessage, req);
    }

    // Handle DB constraint violations (unique keys, FK, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                   HttpServletRequest req) {

        Throwable cause = ex.getMostSpecificCause();

        // If Hibernate provides constraint name, use it
        ConstraintViolationException hib = findHibernateConstraintViolation(ex);
        if (hib != null) {
            String constraint = hib.getConstraintName(); // e.g. uk_users_email

            // Map constraint names -> user-friendly messages
            if (constraint != null) {
                String msg = switch (constraint) {
                    case "uk_users_email" -> "Email already exists";
                    case "uk_users_username" -> "Username already exists";
                    case "uk_refresh_token_token_hash" -> "Token hash already exists";
                    case "uk_business_members_user_business" -> "Business member user already exists";
                    case "uk_customer_profile_user" -> "Customer profile already exists";
                    case "uk_customer_profile_referral_code" -> "Referral code already exists";
                    default -> "Data constraint violation";
                };
                return error(HttpStatus.BAD_REQUEST, msg, req);
            }
        }

        // Fallback (when constraint name isn't available)
        String fallback = (cause != null && cause.getMessage() != null)
                ? "Data integrity violation"
                : "Data integrity violation";

        return error(HttpStatus.BAD_REQUEST, fallback, req);
    }

    private ConstraintViolationException findHibernateConstraintViolation(Throwable ex) {
        Throwable cur = ex;
        while (cur != null) {
            if (cur instanceof ConstraintViolationException cve) return cve;
            cur = cur.getCause();
        }
        return null;
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex, HttpServletRequest req) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", req);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(ApiException ex, HttpServletRequest req) {
        return error(ex.getStatus(), ex.getMessage(), req);
    }
}