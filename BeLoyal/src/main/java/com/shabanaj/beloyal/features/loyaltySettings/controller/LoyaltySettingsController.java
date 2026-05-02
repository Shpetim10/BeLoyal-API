package com.shabanaj.beloyal.features.loyaltySettings.controller;

import com.shabanaj.beloyal.features.loyaltySettings.dto.CreateLoyaltySettingsDto;
import com.shabanaj.beloyal.features.loyaltySettings.dto.LoyaltySettingsDto;
import com.shabanaj.beloyal.features.loyaltySettings.dto.UpdateLoyaltySettingsDto;
import com.shabanaj.beloyal.features.loyaltySettings.service.LoyaltySettingsService;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/loyalty-settings")
@RequiredArgsConstructor
public class LoyaltySettingsController {
    private final LoyaltySettingsService service;

    @GetMapping
    @PreAuthorize("@businessSecurity.hasAccessToCheckStatus(#businessId, authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<LoyaltySettingsDto> getLoyaltySettings(@PathVariable("businessId") Long businessId) {
        LoyaltySettings settings= service.getLoyaltySettings(businessId);

        LoyaltySettingsDto dto=new LoyaltySettingsDto();
        dto.setMaxPointsToRedeem(settings.getMaxPointsToRedeem());
        dto.setMinPointsToRedeem(settings.getMinPointsToRedeem());
        dto.setPointsPerUnitDiscount(settings.getPointsPerUnitDiscount());
        dto.setExpiryType(settings.getExpiryType());
        dto.setMonthsToExpire(settings.getMonthsToExpire());
        dto.setMaxPointsPerTransactions(settings.getMaxPointsPerTransactions());

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("@businessSecurity.hasAccessToCheckStatus(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> createLoyaltySettings(@PathVariable("businessId") Long businessId, @Valid @RequestBody CreateLoyaltySettingsDto dto) {
        service.createLoyaltySettings(businessId, dto);

        return ResponseEntity.ok(Map.of("message", "Loyalty settings were created successfully"));
    }

    @PostMapping("/default")
    @PreAuthorize("@businessSecurity.hasAccessToCheckStatus(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> createDefaultLoyaltySettings(@PathVariable("businessId") Long businessId) {
        service.createDefaultLoyaltySettings(businessId);

        return ResponseEntity.ok(Map.of("message", "Loyalty settings were created successfully"));
    }

    @PatchMapping
    @PreAuthorize("@businessSecurity.hasAccessToCheckStatus(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> patchLoyaltySettings(@PathVariable("businessId") Long businessId, @Valid @RequestBody UpdateLoyaltySettingsDto dto) {
        service.patchLoyaltySettings(businessId, dto);

        return ResponseEntity.ok(Map.of("message", "Loyalty settings were updated successfully"));
    }
}
