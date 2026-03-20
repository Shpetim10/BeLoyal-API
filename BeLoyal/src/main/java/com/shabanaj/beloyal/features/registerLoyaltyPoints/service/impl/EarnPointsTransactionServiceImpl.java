package com.shabanaj.beloyal.features.registerLoyaltyPoints.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shabanaj.beloyal.common.Exception.IdempotencyConflictException;
import com.shabanaj.beloyal.common.redis.idempotency.EarnPointsIdempotencyService;
import com.shabanaj.beloyal.features.billTransaction.repository.BillTransactionRepository;
import com.shabanaj.beloyal.features.billTransaction.service.BillTransactionService;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.businessMember.service.BusinessMemberService;
import com.shabanaj.beloyal.features.earningSettings.service.EarningSettingsService;
import com.shabanaj.beloyal.features.loyaltySettings.service.LoyaltySettingsService;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnComputationResult;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsCommittedEvent;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsTransactionRequest;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnPointsTransactionResponse;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.GuestPointsResult;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.helpers.RequestHasher;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.EarnPointsGuestsCalculatorService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.EarnPointsTransactionService;
import com.shabanaj.beloyal.model.Entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EarnPointsTransactionServiceImpl implements EarnPointsTransactionService {
    private final LoyaltySettingsService loyaltySettingsService;
    private final EarningSettingsService earningSettingsService;
    private final BusinessService businessService;
    private final BusinessMemberService businessMemberService;
    private final EarnPointsGuestsCalculatorService earnPointsGuestsCalculatorService;
    private final BillTransactionService billTransactionService;

    // Idempotency dependencies
    private final EarnPointsIdempotencyService idempotencyService;
    private final ApplicationEventPublisher eventPublisher;
    private final BillTransactionRepository billTransactionRepository;
    private final PointsTransactionRepository pointsTransactionRepository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    @Override
    public EarnPointsTransactionResponse earnPoints(Long businessId, Long userId, String idempotencyKey, EarnPointsTransactionRequest request) {
        // validate request
        validateRequest(businessId, request);

        // compute hash
        String requestHash = RequestHasher.hashEarnPointsRequest(businessId, request);

        // Redis idempotency gate
        String cachedResponse = idempotencyService.beginOrReplay(businessId, idempotencyKey, requestHash);
        if (cachedResponse != null) {
            try {
                return objectMapper.readValue(cachedResponse, EarnPointsTransactionResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize cached response", e);
            }
        }

        try {
            return transactionTemplate.execute(status -> processEarnPointsRequest(businessId, userId, idempotencyKey, request, requestHash));
        } catch (DataIntegrityViolationException ex) {
            // DB duplicate fallback
            Optional<BillTransaction> existingTxOpt = billTransactionRepository.findByBusinessIdAndIdempotencyKey(businessId, idempotencyKey);

            if (existingTxOpt.isPresent()) {
                BillTransaction existingTx = existingTxOpt.get();
                if (!existingTx.getRequestHash().equals(requestHash)) {
                    idempotencyService.clearProcessing(businessId, idempotencyKey);
                    throw new IdempotencyConflictException("Idempotency key already used with different payload");
                }

                // reconstruct response for replay
                EarnPointsTransactionResponse replayedResponse = reconstructResponseForReplay(existingTx);
                idempotencyService.clearProcessing(businessId, idempotencyKey);
                return replayedResponse;
            }

            idempotencyService.clearProcessing(businessId, idempotencyKey);
            throw ex;
        } catch (Exception ex) {
            // Clear processing key if transaction failed due to other exceptions
            idempotencyService.clearProcessing(businessId, idempotencyKey);
            throw ex;
        }
    }

    private EarnPointsTransactionResponse processEarnPointsRequest(Long businessId, Long userId, String idempotencyKey, EarnPointsTransactionRequest request, String requestHash) {
        // find business
        Business business = businessService.getActiveBusinessById(businessId);

        // find businessMember
        BusinessMember businessMember = businessMemberService.getBusinessMemberByUserIdAndBusinessId(userId, businessId);

        // find loyalty settings
        LoyaltySettings loyaltySettings = loyaltySettingsService.getLoyaltySettings(businessId);

        // find earning settings
        EarningSettings earningSettings = earningSettingsService.getEarningSettings(businessId);

        // calculate points to earn
        EarnComputationResult earnComputationResult = earnPointsGuestsCalculatorService.computeEarnPreview(
                business,
                request.getBillAmount(),
                request.getGuestIds(),
                request.getGuestIds().get(0),
                earningSettings,
                loyaltySettings
        );

        // Persist Bill transaction
        BillTransaction billTransaction = BillTransaction.builder()
                .business(business)
                .businessMember(businessMember)
                .billAmount(request.getBillAmount())
                .netAmount(request.getBillAmount())
                .discountAmount(new BigDecimal("0.00"))
                .invoiceReference(request.getInvoiceNumber())
                .note(request.getNote())
                .idempotencyKey(idempotencyKey)
                .requestHash(requestHash)
                .build();

        billTransaction = billTransactionService.save(billTransaction);

        // Persist points transaction and create points bucket for each guest
        earnPointsGuestsCalculatorService.distributeAndPersistPointsTransactionsAndPointsBuckets(
                business,
                businessMember,
                billTransaction,
                earnComputationResult.getGuestResults(),
                earningSettings,
                loyaltySettings
        );

        EarnPointsTransactionResponse response = mapToResponse(loyaltySettings, earningSettings, earnComputationResult, billTransaction);
        response.setStatus("CREATED");

        // Publish event to run after commit
        eventPublisher.publishEvent(new EarnPointsCommittedEvent(this, businessId, idempotencyKey, requestHash, response));

        return response;
    }

    private EarnPointsTransactionResponse reconstructResponseForReplay(BillTransaction existingTx) {
        List<PointsTransaction> pointsTransactions = pointsTransactionRepository.findAllByBillTransactionId(existingTx.getId());

        List<GuestPointsResult> guestResults = pointsTransactions.stream().map(pt -> {
            GuestPointsResult gpr = new GuestPointsResult();
            gpr.setCustomerId(pt.getLoyaltyAccount().getCustomerProfile().getId());
            gpr.setEarnedPoints(pt.getPointsDelta());
            gpr.setCurrentBalance(pt.getLoyaltyAccount().getAvailablePoints());
            return gpr;
        }).toList();

        Integer totalPoints = guestResults.stream().mapToInt(GuestPointsResult::getEarnedPoints).sum();
        Integer remainder = totalPoints % Math.max(1, guestResults.size());

        Long primaryCustomerId = guestResults.isEmpty() ? null : guestResults.get(0).getCustomerId();
        Integer pointsPer = pointsTransactions.isEmpty() ? null : pointsTransactions.get(0).getRulePointsPer();
        BigDecimal amountPer = pointsTransactions.isEmpty() ? null : pointsTransactions.get(0).getRuleAmountPer();

        LoyaltySettings loyaltySettings = loyaltySettingsService.getLoyaltySettings(existingTx.getBusiness().getId());

        return EarnPointsTransactionResponse.builder()
                .status("REPLAYED")
                .billAmount(existingTx.getBillAmount())
                .transactionReference(existingTx.getInvoiceReference())
                .note(existingTx.getNote())
                .totalPoints(totalPoints)
                .remainingPoints(remainder)
                .primaryCustomerId(primaryCustomerId)
                .pointsPer(pointsPer)
                .amountPer(amountPer)
                .maxPointsPerTransaction(loyaltySettings.getMaxPointsPerTransactions())
                .guestPointsResults(guestResults)
                .build();
    }

    private void validateRequest(Long businessId, EarnPointsTransactionRequest request) {
        if (businessId == null || request == null ||
                request.getGuests() == null || request.getGuests().isEmpty()) {
            throw new InvalidParameterException("Parameters are invalid");
        }

        if (request.getBillAmount() == null || request.getBillAmount().compareTo(BigDecimal.ZERO) != 1) {
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
