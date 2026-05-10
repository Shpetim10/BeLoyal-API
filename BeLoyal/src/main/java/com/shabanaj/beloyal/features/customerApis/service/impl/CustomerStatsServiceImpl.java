package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.common.Exception.UserNotFound;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerSummaryDto;
import com.shabanaj.beloyal.features.customerApis.service.CustomerStatsService;
import com.shabanaj.beloyal.features.customerCoupon.service.CouponRedemptionService;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.loyaltyCard.dto.CustomerPointsSummary;
import com.shabanaj.beloyal.features.loyaltyCard.service.CustomerPointsCalculatorService;
import com.shabanaj.beloyal.features.loyaltyCard.service.LoyaltyCardService;
import com.shabanaj.beloyal.features.loyaltySettings.repository.LoyaltySettingsRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.impl.CustomerProfileServiceImpl;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;
import com.shabanaj.beloyal.model.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerStatsServiceImpl implements CustomerStatsService {
    private final UserService userService;
    private final CustomerProfileServiceImpl customerProfileService;
    private final LoyaltyCardService loyaltyCardService;
    private final CustomerPointsCalculatorService customerPointsCalculatorService;
    private final CouponRedemptionService couponRedemptionService;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final LoyaltySettingsRepository loyaltySettingsRepository;

    @Override
    public CustomerSummaryDto getCustomerStats(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFound("This user does not exist!"));

        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);
        LoyaltyCard loyaltyCard = loyaltyCardService.getLoyaltyCardForUser(user);

        return mapToCustomerSummary(customerProfile, loyaltyCard);
    }

    private CustomerSummaryDto mapToCustomerSummary(CustomerProfile customerProfile, LoyaltyCard loyaltyCard) {
        String memberSinceLabel = customerProfile.getCreatedAt().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + customerProfile.getCreatedAt().getYear();
        CustomerPointsSummary summary = customerPointsCalculatorService.getCustomerPointsSummary(customerProfile);
        int activeRewards = computeActiveRewards(customerProfile);

        return new CustomerSummaryDto(
                summary.currentPoints(),
                summary.lifetimePoints(),
                summary.lifetimeSpentPoints(),
                summary.businessesVisited(),
                couponRedemptionService.getCustomerCouponsCount(customerProfile),
                activeRewards,
                memberSinceLabel,
                loyaltyCard.getManualCode()
        );
    }

    private int computeActiveRewards(CustomerProfile customerProfile) {
        List<LoyaltyAccount> accounts = loyaltyAccountRepository
                .findAllWithBusinessByCustomerProfileId(customerProfile.getId());
        if (accounts.isEmpty()) return 0;

        Set<Long> businessIds = accounts.stream()
                .map(la -> la.getBusiness().getId())
                .collect(Collectors.toSet());

        Map<Long, LoyaltySettings> settingsMap = loyaltySettingsRepository
                .findAllByBusinessIdIn(businessIds)
                .stream()
                .collect(Collectors.toMap(s -> s.getBusiness().getId(), s -> s));

        return (int) accounts.stream()
                .filter(la -> {
                    LoyaltySettings settings = settingsMap.get(la.getBusiness().getId());
                    return settings != null && la.getAvailablePoints() >= settings.getMinPointsToRedeem();
                })
                .count();
    }
}
