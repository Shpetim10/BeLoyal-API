package com.shabanaj.beloyal.features.registerLoyaltyPoints.service.impl;

import com.shabanaj.beloyal.common.Exception.InvalidQrCodeException;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.earningSettings.service.EarningSettingsService;
import com.shabanaj.beloyal.features.loyaltySettings.service.LoyaltySettingsService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.CouponDiscountResult;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnComputationResult;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.PointsPreviewRequest;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.PointsPreviewResponse;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.CouponDiscountCalculatorService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.EarnPointsGuestsCalculatorService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.PointsPreviewService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CustomerCoupon;
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
    private final CustomerCouponRepository customerCouponRepository;
    private final CouponDiscountCalculatorService couponDiscountCalculatorService;

    @Override
    public PointsPreviewResponse preview(Long businessId, PointsPreviewRequest request) {
        validatePreviewRequest(businessId, request);

        Business business = businessService.getActiveBusinessById(businessId);
        EarningSettings earningSettings = earningSettingsService.getEarningSettings(businessId);
        LoyaltySettings loyaltySettings = loyaltySettingsService.getLoyaltySettings(businessId);

        CouponDiscountResult couponDiscountResult = null;
        BigDecimal effectiveBillAmount = request.getBillAmount();

        if (request.getCouponQrCode() != null && !request.getCouponQrCode().isBlank()) {
            CustomerCoupon customerCoupon = customerCouponRepository
                    .findByQrCode(request.getCouponQrCode())
                    .orElseThrow(InvalidQrCodeException::new);

            if (!customerCoupon.getBusiness().getId().equals(businessId)) {
                throw new InvalidQrCodeException();
            }

            couponDiscountResult = couponDiscountCalculatorService.calculate(customerCoupon, request.getBillAmount());
            effectiveBillAmount = couponDiscountResult.getFinalAmount();
        }

        EarnComputationResult earnComputationResult = earnPointsGuestsCalculatorService.computeEarnPreview(
                business,
                effectiveBillAmount,
                request.getGuestIds(),
                request.getGuestIds().get(0),
                earningSettings,
                loyaltySettings
        );

        return mapToResponse(earningSettings, loyaltySettings, earnComputationResult, request.getBillAmount(), couponDiscountResult);
    }

    private void validatePreviewRequest(Long businessId, PointsPreviewRequest request) {
        if (businessId == null || request == null ||
                request.getGuests() == null || request.getGuests().isEmpty()) {
            throw new InvalidParameterException("Parameters are invalid");
        }

        if (request.getBillAmount() == null || request.getBillAmount().compareTo(BigDecimal.ZERO) != 1) {
            throw new InvalidParameterException("Bill amount must be greater than zero");
        }

        List<Long> customerIds = request.getGuests().stream()
                .map(PointsPreviewRequest.Guest::getCustomerId)
                .toList();

        if (new HashSet<>(customerIds).size() != customerIds.size()) {
            throw new InvalidParameterException("Duplicate guests are not allowed");
        }
    }

    private PointsPreviewResponse mapToResponse(
            EarningSettings earningSettings,
            LoyaltySettings loyaltySettings,
            EarnComputationResult earnComputationResult,
            BigDecimal originalBillAmount,
            CouponDiscountResult couponDiscountResult) {
        PointsPreviewResponse response = new PointsPreviewResponse();
        response.setTotalPoints(earnComputationResult.getTotalPoints());
        response.setRemainingPoints(earnComputationResult.getRemainder());
        response.setPrimaryCustomerId(earnComputationResult.getPrimaryCustomerId());
        response.setPointsPer(earningSettings.getPointsPer());
        response.setAmountPer(earningSettings.getAmountPer());
        response.setMaxPointsPerTransaction(loyaltySettings.getMaxPointsPerTransactions());
        response.setGuestPointsResults(earnComputationResult.getGuestResults());

        if (couponDiscountResult != null) {
            response.setOriginalBillAmount(originalBillAmount);
            response.setCouponDiscountApplied(couponDiscountResult.getDiscountApplied());
            response.setEffectiveBillAmount(couponDiscountResult.getFinalAmount());
            response.setAppliedCustomerCouponId(couponDiscountResult.getCustomerCouponId());
        }

        return response;
    }
}
