package com.shabanaj.beloyal.features.loyaltySettings.service.impl;

import com.shabanaj.beloyal.common.Exception.LoyaltySettingsNotFound;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.loyaltySettings.dto.CreateLoyaltySettingsDto;
import com.shabanaj.beloyal.features.loyaltySettings.dto.UpdateLoyaltySettingsDto;
import com.shabanaj.beloyal.features.loyaltySettings.repository.LoyaltySettingsRepository;
import com.shabanaj.beloyal.features.loyaltySettings.service.LoyaltySettingsService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;
import com.shabanaj.beloyal.model.Enums.ExpiryType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidParameterException;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoyaltySettingsServiceImpl implements LoyaltySettingsService {
    private final LoyaltySettingsRepository  loyaltySettingsRepository;
    private final BusinessService businessService;

    @Override
    public void createDefaultLoyaltySettings(Long businessId) {
        // Create default dto
        CreateLoyaltySettingsDto dto = new CreateLoyaltySettingsDto();
        dto.setExpiryType(ExpiryType.NO_EXPIRY);
        dto.setMinPointsToRedeem(1);
        dto.setMaxPointsToRedeem(1);
        dto.setMaxPointsPerTransaction(100);
        dto.setMonthsToExpire(0);
        dto.setPointsPerUnitDiscount(5);

        // Call create method
        createLoyaltySettings(
                businessId,
                dto);
    }

    @Override
    public void createLoyaltySettings(Long businessId, CreateLoyaltySettingsDto dto) {
        if(businessId == null || dto == null) {
            throw new InvalidParameterException("businessId and dto cannot be null");
        }

        // Find business
        Business business = businessService.getBusinessById(businessId);

        // Create Loyalty settings
        LoyaltySettings loyaltySettings = new LoyaltySettings();
        loyaltySettings.setBusiness(business);
        loyaltySettings.setMinPointsToRedeem(dto.getMinPointsToRedeem());
        loyaltySettings.setMaxPointsToRedeem(dto.getMaxPointsToRedeem());
        loyaltySettings.setPointsPerUnitDiscount(dto.getPointsPerUnitDiscount());
        loyaltySettings.setMaxPointsPerTransactions(dto.getMaxPointsPerTransaction());
        loyaltySettings.setExpiryType(dto.getExpiryType());

        if(dto.getExpiryType().equals(ExpiryType.NO_EXPIRY)){
            loyaltySettings.setMonthsToExpire(0);
        }else{
            if(dto.getMonthsToExpire()<=0){
                throw new InvalidParameterException("You have selected an expiration model of points but not specified the number of months");
            }
            loyaltySettings.setMonthsToExpire(dto.getMonthsToExpire());
        }

        // Persist
        loyaltySettingsRepository.save(loyaltySettings);
    }

    @Override
    public void patchLoyaltySettings(Long businessId, UpdateLoyaltySettingsDto dto) {
        if (businessId == null || dto == null) {
            throw new InvalidParameterException("businessId and dto cannot be null");
        }

        // Find Loyalty Settings
        try{
            //Find loyalty Settings
            LoyaltySettings loyaltySettings=getLoyaltySettings(businessId);

            // Update
            loyaltySettings.setMinPointsToRedeem(dto.getMinPointsToRedeem());
            loyaltySettings.setMaxPointsToRedeem(dto.getMaxPointsToRedeem());
            loyaltySettings.setPointsPerUnitDiscount(dto.getPointsPerUnitDiscount());
            loyaltySettings.setMaxPointsPerTransactions(dto.getMaxPointsPerTransaction());
            loyaltySettings.setExpiryType(dto.getExpiryType());

            if(dto.getExpiryType().equals(ExpiryType.NO_EXPIRY)){
                loyaltySettings.setMonthsToExpire(0);
            }else{
                if(dto.getMonthsToExpire()<=0){
                    throw new InvalidParameterException("You have selected an expiration model of points but not specified the number of months");
                }

                loyaltySettings.setMonthsToExpire(dto.getMonthsToExpire());
            }

            // configuration and enabled status update
            if(!loyaltySettings.isConfigured() || !loyaltySettings.isEnabled()){
                loyaltySettings.setEnabled(true);
                loyaltySettings.setConfigured(true);
            }

            loyaltySettingsRepository.save(loyaltySettings);
        } catch(LoyaltySettingsNotFound ex){
            createLoyaltySettings(businessId, dto);
        }
    }

    @Override
    public LoyaltySettings getLoyaltySettings(Long businessId){
        Optional<LoyaltySettings> loyaltySettings= loyaltySettingsRepository.findByBusinessId(businessId);

        if(loyaltySettings.isEmpty()){
            throw new LoyaltySettingsNotFound("Loyalty settings were not found");
        }

        return loyaltySettings.get();
    }
}
