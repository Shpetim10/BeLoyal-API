package com.shabanaj.beloyal.features.loyaltySettings.service;

import com.shabanaj.beloyal.features.loyaltySettings.dto.CreateLoyaltySettingsDto;
import com.shabanaj.beloyal.features.loyaltySettings.dto.UpdateLoyaltySettingsDto;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;

public interface LoyaltySettingsService {
    void createDefaultLoyaltySettings(Long businessId);
    void createLoyaltySettings(Long businessId, CreateLoyaltySettingsDto dto);
    void patchLoyaltySettings(Long businessId, UpdateLoyaltySettingsDto dto);
    LoyaltySettings getLoyaltySettings(Long businessId);
}
