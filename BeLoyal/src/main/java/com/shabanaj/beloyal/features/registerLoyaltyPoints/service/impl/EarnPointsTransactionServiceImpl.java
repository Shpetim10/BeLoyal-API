package com.shabanaj.beloyal.features.registerLoyaltyPoints.service.impl;

import com.shabanaj.beloyal.features.billTransaction.service.BillTransactionService;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberService;
import com.shabanaj.beloyal.features.earningSettings.service.EarningSettingsService;
import com.shabanaj.beloyal.features.loyaltySettings.service.LoyaltySettingsService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnComputationResult;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsTransactionRequest;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsTransactionResponse;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.PointsPreviewRequest;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.EarnPointsGuestsCalculatorService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.EarnPointsTransactionService;
import com.shabanaj.beloyal.model.Entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EarnPointsTransactionServiceImpl implements EarnPointsTransactionService {
    private final LoyaltySettingsService loyaltySettingsService;
    private final EarningSettingsService earningSettingsService;
    private final BusinessService businessService;
    private final BusinessMemberService businessMemberService;
    private final EarnPointsGuestsCalculatorService earnPointsGuestsCalculatorService;
    private final BillTransactionService billTransactionService;

    @Override
    @Transactional
    public EarnPointsTransactionResponse earnPoints(Long businessId, Long userId, EarnPointsTransactionRequest request) {
        // validate request
        validateRequest(businessId, request);

        // find business
        Business business= businessService.getActiveBusinessById(businessId);

        // find businessMember
        BusinessMember businessMember= businessMemberService.getBusinessMemberByUserIdAndBusinessId(userId, businessId);

        // find loyalty settings
        LoyaltySettings loyaltySettings=loyaltySettingsService.getLoyaltySettings(businessId);

        // find earning settings
        EarningSettings earningSettings=earningSettingsService.getEarningSettings(businessId);

        // calculate points to earn
        EarnComputationResult earnComputationResult= earnPointsGuestsCalculatorService.computeEarnPreview(
                business,
                request.getBillAmount(),
                request.getGuestIds(),
                request.getGuestIds().get(0),
                earningSettings,
                loyaltySettings
        );

        // Persist Bill transaction
        BillTransaction billTransaction= BillTransaction.builder()
                .business(business)
                .businessMember(businessMember)
                .billAmount(request.getBillAmount())
                .netAmount(request.getBillAmount())
                .discountAmount(new BigDecimal("0.00"))
                .invoiceReference(request.getInvoiceNumber())
                .note(request.getNote())
                .build();

        billTransaction= billTransactionService.save(billTransaction);

        // Persist points transaction and create points bucket for each guest
        earnPointsGuestsCalculatorService.distributeAndPersistPointsTransactionsAndPointsBuckets(
                business,
                businessMember,
                billTransaction,
                earnComputationResult.getGuestResults(),
                earningSettings,
                loyaltySettings
        );

        return mapToResponse(loyaltySettings, earningSettings, earnComputationResult, billTransaction);
    }

    private void validateRequest(Long businessId, EarnPointsTransactionRequest request) {
        if (businessId == null || request == null ||
                request.getGuests() == null || request.getGuests().isEmpty()) {
            throw new InvalidParameterException("Parameters are invalid");
        }

        if (request.getBillAmount() == null || request.getBillAmount().compareTo(BigDecimal.ZERO)!= 1) {
            throw new InvalidParameterException("Bill amount must be greater than zero");
        }

        // handle duplications
        List<Long> customerIds = request.getGuests().stream()
                .map(EarnPointsTransactionRequest.Guest::getCustomerId)
                .toList();

        if (new HashSet<>(customerIds).size() != customerIds.size()) {
            throw new InvalidParameterException("Duplicate guests are not allowed");
        }
    }

    private EarnPointsTransactionResponse mapToResponse(
            LoyaltySettings loyaltySettings,
            EarningSettings earningSettings,
            EarnComputationResult earnComputationResult,
            BillTransaction billTransaction) {
        return EarnPointsTransactionResponse.builder()
                .billAmount(billTransaction.getBillAmount())
                .transactionReference(billTransaction.getInvoiceReference())
                .note(billTransaction.getNote())
                .totalPoints(earnComputationResult.getTotalPoints())
                .remainingPoints(earnComputationResult.getRemainder())
                .primaryCustomerId(earnComputationResult.getPrimaryCustomerId())
                .pointsPer(earningSettings.getPointsPer())
                .amountPer(earningSettings.getAmountPer())
                .maxPointsPerTransaction(loyaltySettings.getMaxPointsPerTransactions())
                .guestPointsResults(earnComputationResult.getGuestResults())
                .build();
    }
}
