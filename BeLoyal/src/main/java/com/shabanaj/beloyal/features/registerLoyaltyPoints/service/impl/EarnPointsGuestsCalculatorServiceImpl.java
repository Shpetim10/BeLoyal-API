package com.shabanaj.beloyal.features.registerLoyaltyPoints.service.impl;

import com.shabanaj.beloyal.features.loyaltyAccount.service.LoyaltyAccountService;
import com.shabanaj.beloyal.features.pointsBucket.service.PointsBucketService;
import com.shabanaj.beloyal.features.pointsTransaction.service.PointsTransactionService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.EarnComputationResult;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.dto.GuestPointsResult;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.EarnPointsGuestsCalculatorService;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.PointsCalculatorService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.ExpiryType;
import com.shabanaj.beloyal.model.Enums.PointsBucketStatus;
import com.shabanaj.beloyal.model.Enums.PointsType;
import com.shabanaj.beloyal.model.Enums.ScanMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EarnPointsGuestsCalculatorServiceImpl implements EarnPointsGuestsCalculatorService {
    private final PointsCalculatorService pointsCalculatorService;
    private final CustomerProfileService customerProfileService;
    private final LoyaltyAccountService loyaltyAccountService;
    private final PointsTransactionService pointsTransactionService;
    private final Clock clock;
    private final PointsBucketService pointsBucketService;

    @Override
    public EarnComputationResult computeEarnPreview(Business business, BigDecimal billAmount, List<Long> guestIds, Long primaryGuestId, EarningSettings earningSettings, LoyaltySettings loyaltySettings) {
        // calculate points
        int points = pointsCalculatorService.calculatePoints(
                billAmount,
                earningSettings
        );

        // apply caps
        points = pointsCalculatorService.applyCaps(points, loyaltySettings);

        // calculate for each guest
        int guestCount = guestIds.size();
        int equalShare = points / guestCount;
        int remainder = points % guestCount;

        // distribute points
        List<GuestPointsResult> guestEarnPointsList = distributePoints(
                business,
                guestIds,
                equalShare,
                remainder
        );

        // construct result
        EarnComputationResult  result = new EarnComputationResult();
        result.setTotalPoints(points);
        result.setEqualShare(equalShare);
        result.setRemainder(remainder);
        result.setPrimaryCustomerId(guestEarnPointsList.get(0).getCustomerId());
        result.setGuestResults(guestEarnPointsList);

        return result;
    }

    @Override
    public List<GuestPointsResult> distributePoints(Business business, List<Long> guestIds, int equalShare, int remainder){
        List<GuestPointsResult> guestEarnPointsList = new ArrayList<>();

        for (Long guestId: guestIds) {
            GuestPointsResult guest= new GuestPointsResult();
            guest.setCustomerId(guestId);
            guest.setEarnedPoints(equalShare);

            CustomerProfile customerProfile = customerProfileService.getCustomerProfileById(guestId);

            int currentPoints = loyaltyAccountService
                    .getLoyaltyAccountByCustomerProfileAndBusiness(customerProfile, business)
                    .getAvailablePoints();

            guest.setCurrentBalance(currentPoints + equalShare);

            guestEarnPointsList.add(guest);
        }

        if (remainder != 0) {
            GuestPointsResult firstGuest = guestEarnPointsList.get(0);
            firstGuest.setEarnedPoints(firstGuest.getEarnedPoints() + remainder);
            firstGuest.setCurrentBalance(firstGuest.getCurrentBalance() + remainder);
        }

        return guestEarnPointsList;
    }

    @Override
    public void distributeAndPersistPointsTransactionsAndPointsBuckets(Business business, BusinessMember businessMember, BillTransaction billTransaction, List<GuestPointsResult> guestResults, EarningSettings earningSettings, LoyaltySettings loyaltySettings) {
        for (GuestPointsResult result:  guestResults) {
            // find customer profile
            CustomerProfile customerProfile= customerProfileService.getCustomerProfileById(result.getCustomerId());
            // find loyalty account
            LoyaltyAccount loyaltyAccount = loyaltyAccountService.findLoyaltyAccountOrCreate(customerProfile, business);

            // update points balance
            loyaltyAccount.add(result.getEarnedPoints());
            loyaltyAccountService.save(loyaltyAccount);

            // create points transaction
            PointsTransaction pointsTransaction= PointsTransaction.builder()
                    .loyaltyAccount(loyaltyAccount)
                    .businessMember(businessMember)
                    .billTransaction(billTransaction)
                    .type(PointsType.EARN_BILL)
                    .reason("You earned points at "+ business.getBusinessName()+ " on "+ billTransaction.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .description(getEarnBillTransactionMessage(business, billTransaction, guestResults, result))
                    .scanMethod(ScanMethod.QR_CODE)
                    .moneyAmount(billTransaction.getBillAmount())
                    .pointsDelta(result.getEarnedPoints())
                    .rulePointsPer(earningSettings.getPointsPer())
                    .ruleAmountPer(earningSettings.getAmountPer())
                    .build();

            //persist points transaction
            pointsTransactionService.save(pointsTransaction);

            // calculate expiration
            LocalDateTime now = LocalDateTime.now(clock);
            LocalDateTime expiresAt= null;

            if(loyaltySettings.getExpiryType()== ExpiryType.EXPIRE_AFTER_X_MONTHS && loyaltySettings.getMonthsToExpire()!=null)
                expiresAt = now.plusMonths(loyaltySettings.getMonthsToExpire());

            // create points bucket
            PointsBucket pointsBucket= PointsBucket.builder()
                    .loyaltyAccount(loyaltyAccount)
                    .sourceTransaction(pointsTransaction)
                    .pointsEarned(result.getEarnedPoints())
                    .pointsRemaining(result.getEarnedPoints())
                    .status(PointsBucketStatus.ACTIVE)
                    .earnedAt(now)
                    .expiresAt(expiresAt)
                    .build();

            //persist
            pointsBucketService.save(pointsBucket);
        }
    }


    // helpers
    private String getEarnBillTransactionMessage(Business business, BillTransaction billTransaction, List<GuestPointsResult> guestResults, GuestPointsResult currentGuestResult){
        // create builder
        StringBuilder description = new StringBuilder();

        // append base message
        description.append("You earned points at ").append(business.getBusinessName()).append(" on ").append(billTransaction.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append(" for a bill of value ").append(billTransaction.getBillAmount()).append(" ").append(business.getCurrencyCode().getDisplayName()).append(" you paid. ");
        // append people who split the bill
        if(guestResults.size()>1){
            description.append("The points for this bill were distributed among: ");
            for(int i=0;i<guestResults.size();i++){
                CustomerProfile customerProfile= customerProfileService.getCustomerProfileById(guestResults.get(i).getCustomerId());
                description.append(customerProfile.getUser().getFirstName()).append(" ").append(customerProfile.getUser().getLastName());

                if(i<guestResults.size()-2){
                    description.append(", ");
                }
                else if(i==guestResults.size()-2){
                    description.append(" and ");
                }
                else{
                    description.append(". ");
                }
            }
        }

        // append the current guest points
        description.append(" You were awarded with ").append(currentGuestResult.getEarnedPoints()).append(" points.");

        // return
        return description.toString();
    }
}
