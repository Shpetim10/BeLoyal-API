package com.shabanaj.beloyal.common.Configurations;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

@Component
public class JsonHelperConfig {

    public static boolean isPresent(JsonNullable<?> field) {
        return field != null && field.isPresent();
    }

    public static String safe(String s) {
        return s == null ? "" : s;
    }

    public static String normalizeRequired(JsonNullable<String> field, String fieldName) throws BadRequestException {
        // field is present (checked by caller)
        String value = field.orElse(null);
        if (value == null) throw new BadRequestException(fieldName + " cannot be null");

        value = value.trim();
        if (value.isBlank()) throw new BadRequestException(fieldName + " cannot be blank");

        return value;
    }

    public static String normalizeOptional(JsonNullable<String> field) {
        if (field == null || !field.isPresent()) return null;

        // present:
        String value = field.orElse(null); // null => clear
        if (value == null) return null;

        value = value.trim();

        return value.isBlank() ? null : value;
    }

    public static <T> void applyRequiredEnum(JsonNullable<T> field, String name, java.util.function.Consumer<T> setter) throws BadRequestException {
        if (field == null || !field.isPresent()) return; // not sent
        T value = field.orElse(null);
        if (value == null) throw new BadRequestException(name + " cannot be null");
        setter.accept(value);
    }

    public static <T> void applyOptionalEnum(JsonNullable<T> field, java.util.function.Consumer<T> setter) {
        if (field == null || !field.isPresent()) return;   // not sent
        setter.accept(field.orElse(null));                 // null => clear
    }

    public static void applyRequiredString(JsonNullable<String> field, String name, java.util.function.Consumer<String> setter) throws BadRequestException {
        if (field == null || !field.isPresent()) return; // not sent
        setter.accept(normalizeRequired(field, name));
    }

    public static void applyOptionalString(JsonNullable<String> field, java.util.function.Consumer<String> setter) {
        if (field == null || !field.isPresent()) return; // not sent
        setter.accept(normalizeOptional(field));
    }

    public static void applyRequiredBoolean(JsonNullable<Boolean> field,
                                             String name,
                                             java.util.function.Consumer<Boolean> setter) throws BadRequestException {
        if (field == null || !field.isPresent()) return;
        Boolean v = field.orElse(null);
        if (v == null) throw new BadRequestException(name + " cannot be null");
        setter.accept(v);
    }

    public static void applyOptionalBoolean(JsonNullable<Boolean> field,
                                             java.util.function.Consumer<Boolean> setter) {
        if (field == null || !field.isPresent()) return; // not sent
        setter.accept(field.orElse(null));               // null => clear
    }

    public static void applyRequiredDecimal(JsonNullable<java.math.BigDecimal> field,
                                            String name,
                                            java.math.BigDecimal min,
                                            java.util.function.Consumer<java.math.BigDecimal> setter) throws BadRequestException {
        if (field == null || !field.isPresent()) return; // not sent
        java.math.BigDecimal value = field.orElse(null);
        if (value == null) throw new BadRequestException(name + " cannot be null");
        if (value.compareTo(min) < 0) throw new BadRequestException(name + " must be >= " + min.toPlainString());
        setter.accept(value);
    }
}
