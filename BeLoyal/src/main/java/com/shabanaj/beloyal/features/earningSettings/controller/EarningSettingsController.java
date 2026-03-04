package com.shabanaj.beloyal.features.earningSettings.controller;

import com.shabanaj.beloyal.features.earningSettings.dto.CreateEarningSettingsDto;
import com.shabanaj.beloyal.features.earningSettings.dto.EarningSettingsDto;
import com.shabanaj.beloyal.features.earningSettings.dto.UpdateEarningSettingsDto;
import com.shabanaj.beloyal.features.earningSettings.service.EarningSettingsService;
import com.shabanaj.beloyal.model.Entity.EarningSettings;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/besahub/business/{businessId}/earning-settings")
@RequiredArgsConstructor
public class EarningSettingsController {
    private final EarningSettingsService earningSettingsService;

    @PostMapping("/default")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> createDefaultEarningSettings(@PathVariable("businessId") Long businessId) {
        earningSettingsService.createDefaultEarningSettings(businessId);

        return ResponseEntity.ok(Map.of("message", "Default earning settings was set successfully!"));
    }

    @PostMapping("")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> createEarningSettings(@PathVariable("businessId") Long businessId, @RequestBody @Valid CreateEarningSettingsDto dto) {
        earningSettingsService.createEarningSettings(businessId, dto);

        return ResponseEntity.ok(Map.of("message", "Default earning settings was successfully!"));
    }

    @PatchMapping("")
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN')")
    public ResponseEntity<Map<String, String>> updateEarningSettings(@PathVariable("businessId") Long businessId, @RequestBody @Valid UpdateEarningSettingsDto updateEarningSettingsDto){
        earningSettingsService.updateEarningSettings(businessId, updateEarningSettingsDto);

        return ResponseEntity.ok(Map.of("message", "Earning settings was updated successfully!"));
    }

    @GetMapping()
    @PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, 'BUSINESS_ADMIN', 'STAFF')")
    public ResponseEntity<EarningSettingsDto> getEarningSettings(@PathVariable("businessId") Long businessId){
        EarningSettings earningSettings = earningSettingsService.getEarningSettings(businessId);

        EarningSettingsDto earningSettingsDto = new EarningSettingsDto();
        earningSettingsDto.setAmountPer(earningSettings.getAmountPer());
        earningSettingsDto.setPointsPer(earningSettings.getPointsPer());

        return ResponseEntity.ok(earningSettingsDto);
    }
}
