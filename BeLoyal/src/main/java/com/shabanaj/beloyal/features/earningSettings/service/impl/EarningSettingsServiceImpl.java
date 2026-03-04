package com.shabanaj.beloyal.features.earningSettings.service.impl;

import com.shabanaj.beloyal.common.Exception.EarningSettingsNotFound;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.earningSettings.dto.CreateEarningSettingsDto;
import com.shabanaj.beloyal.features.earningSettings.dto.UpdateEarningSettingsDto;
import com.shabanaj.beloyal.features.earningSettings.repository.EarningSettingsRepository;
import com.shabanaj.beloyal.features.earningSettings.service.EarningSettingsService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.EarningSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EarningSettingsServiceImpl implements EarningSettingsService {
    private final EarningSettingsRepository earningSettingsRepository;
    private final BusinessService businessService;

    @Override
    public EarningSettings getEarningSettings(Long businessId) {
        Optional<EarningSettings> earningSettings= earningSettingsRepository.findByBusinessId(businessId);

        if(earningSettings.isEmpty()){
            throw new EarningSettingsNotFound("EarningSettings not found for this business");
        }

        return earningSettings.get();
    }

    @Override
    public void createEarningSettings(Long businessId, CreateEarningSettingsDto createEarningSettingsDto) {
        if (businessId == null || createEarningSettingsDto == null) {
            throw new IllegalArgumentException("businessId and createEarningSettingsDto cannot be null");
        }

        // Find business or throw
        Business business= businessService.getBusinessById(businessId);

        // Create Earning settings
        EarningSettings earningSettings = new EarningSettings();
        earningSettings.setBusiness(business);
        earningSettings.setAmountPer(createEarningSettingsDto.getAmountPer());
        earningSettings.setPointsPer(createEarningSettingsDto.getPointsPer());

        // Persist
        earningSettingsRepository.save(earningSettings);
    }

    @Override
    public void createDefaultEarningSettings(Long businessId) {
        // Create default earning setting dto
        CreateEarningSettingsDto createEarningSettingsDto = new CreateEarningSettingsDto();
        createEarningSettingsDto.setAmountPer(100);
        createEarningSettingsDto.setPointsPer(1);

        // call other method
        createEarningSettings(businessId, createEarningSettingsDto);
    }

    @Override
    public void updateEarningSettings(Long businessId, UpdateEarningSettingsDto updateEarningSettingsDto) {
        if (businessId == null || updateEarningSettingsDto == null) {
            throw new IllegalArgumentException("businessId and updateEarningSettingsDto cannot be null");
        }

        // Find existing earning rules
        try {
            EarningSettings earningSettings= getEarningSettings(businessId);

            // Update
            earningSettings.setAmountPer(updateEarningSettingsDto.getAmountPer());
            earningSettings.setPointsPer(updateEarningSettingsDto.getPointsPer());

            // configuration and enabled status update
            if(!earningSettings.isConfigured() || !earningSettings.isEnabled()){
                earningSettings.setEnabled(true);
                earningSettings.setConfigured(true);
            }

            // Persist
            earningSettingsRepository.save(earningSettings);
        } catch(EarningSettingsNotFound e){
            createEarningSettings(businessId, updateEarningSettingsDto);
        }
    }
}
