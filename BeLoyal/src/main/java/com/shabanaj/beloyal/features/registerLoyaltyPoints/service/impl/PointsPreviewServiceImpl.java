package com.shabanaj.beloyal.features.registerLoyaltyPoints.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.earningSettings.service.EarningSettingsService;
import com.shabanaj.beloyal.features.loyaltySettings.service.LoyaltySettingsService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnComputationResult;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.PointsPreviewRequest;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.PointsPreviewResponse;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.EarnPointsGuestsCalculatorService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.PointsPreviewService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.EarningSettings;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointsPreviewServiceImpl implements PointsPreviewService {
    private final BusinessService businessService;
    private final EarningSettingsService earningSettingsService;
    private final LoyaltySettingsService loyaltySettingsService;
    private final EarnPointsGuestsCalculatorService earnPointsGuestsCalculatorService;

    @Override
    public PointsPreviewResponse preview(Long businessId, PointsPreviewRequest request) {
        // check parameters
        validatePreviewRequest(businessId, request);

        // find business
        Business business = businessService.getActiveBusinessById(businessId);

        // find earning and loyalty settings
        EarningSettings earningSettings = earningSettingsService.getEarningSettings(businessId);
        LoyaltySettings loyaltySettings = loyaltySettingsService.getLoyaltySettings(businessId);

        // distribute points among guests
        EarnComputationResult earnComputationResult = earnPointsGuestsCalculatorService.computeEarnPreview(
                business,
                request.getBillAmount(),
                request.getGuestIds(),
                request.getGuestIds().get(0),
                earningSettings,
                loyaltySettings
        );

        return mapToResponse(earningSettings, loyaltySettings, earnComputationResult);
    }

    // helpers
    private void validatePreviewRequest(Long businessId, PointsPreviewRequest request) {
        if (businessId == null || request == null ||
                request.getGuests() == null || request.getGuests().isEmpty()) {
            throw new InvalidParameterException("Parameters are invalid");
        }

        if (request.getBillAmount() == null || request.getBillAmount().compareTo(BigDecimal.ZERO)!= 1) {
            throw new InvalidParameterException("Bill amount must be greater than zero");
        }

        // handle duplications
        List<Long> customerIds = request.getGuests().stream()
                .map(PointsPreviewRequest.Guest::getCustomerId)
                .toList();

        if (new HashSet<>(customerIds).size() != customerIds.size()) {
            throw new InvalidParameterException("Duplicate guests are not allowed");
        }
    }

    private PointsPreviewResponse mapToResponse(EarningSettings earningSettings, LoyaltySettings loyaltySettings, EarnComputationResult earnComputationResult) {
        PointsPreviewResponse response = new PointsPreviewResponse();
        response.setTotalPoints(earnComputationResult.getTotalPoints());
        response.setRemainingPoints(earnComputationResult.getRemainder());
        response.setPrimaryCustomerId(earnComputationResult.getPrimaryCustomerId());
        response.setPointsPer(earningSettings.getPointsPer());
        response.setAmountPer(earningSettings.getAmountPer());
        response.setMaxPointsPerTransaction(loyaltySettings.getMaxPointsPerTransactions());
        response.setGuestPointsResults(earnComputationResult.getGuestResults());

        return response;
    }
}
