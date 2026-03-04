package com.shabanaj.beloyal.features.earningSettings.service;

import com.shabanaj.beloyal.features.earningSettings.dto.CreateEarningSettingsDto;
import com.shabanaj.beloyal.features.earningSettings.dto.UpdateEarningSettingsDto;
import com.shabanaj.beloyal.model.Entity.EarningSettings;

public interface EarningSettingsService {
    EarningSettings getEarningSettings(Long businessId);
    void createEarningSettings(Long businessId, CreateEarningSettingsDto createEarningSettingsDto);
    void createDefaultEarningSettings(Long businessId);
    void updateEarningSettings(Long businessId, UpdateEarningSettingsDto updateEarningSettingsDto);
}
