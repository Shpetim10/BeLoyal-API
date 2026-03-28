package com.shabanaj.beloyal.common.Configurations;

import com.shabanaj.beloyal.common.Exception.ApiException;
import com.shabanaj.beloyal.common.Exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

        logger.error(ex.getMessage(), ex);
        return error(HttpStatus.BAD_REQUEST, errorMessage, req);
    }

    // Handle DB constraint violations (unique keys, FK, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                   HttpServletRequest req) {

        Throwable cause = ex.getMostSpecificCause();

        logger.error(ex.getMessage(), ex);

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
                    case "uk_loyalty_account_unique_profile_and_business"-> "The loyalty account already exists";
                    case "uk_bill_transaction_business_invoice"->"This transaction is already registered for this business";
                    case "uk_points_buckets_loyalty_account_source_transaction" -> "The points bucket already exists";
                    case "uk_points_bucket_consumption_points_transaction_points_bucket" -> "The consumption points bucket already exists";
                    case "uk_business_category_name" -> "A category with this name already exists";
                    case "uk_item_variant"-> "A variant with this name already exists";
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
        logger.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex, HttpServletRequest req) {
        logger.error(ex.getMessage(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", req);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(ApiException ex, HttpServletRequest req) {
        logger.error(ex.getMessage(), ex);
        return error(ex.getStatus(), ex.getMessage(), req);
    }
}