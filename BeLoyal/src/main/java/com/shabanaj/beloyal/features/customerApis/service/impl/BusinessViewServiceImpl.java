package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerBusinessDto;
import com.shabanaj.beloyal.features.customerApis.service.BusinessViewService;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.loyaltySettings.repository.LoyaltySettingsRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.BusinessCategory;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessViewServiceImpl implements BusinessViewService {

    private final UserService userService;
    private final CustomerProfileService customerProfileService;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final LoyaltySettingsRepository loyaltySettingsRepository;
    private final BusinessService businessService;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerBusinessDto> getBusinessesForCustomer(Long userId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);

        List<LoyaltyAccount> accounts = loyaltyAccountRepository
                .findAllWithBusinessByCustomerProfileId(customerProfile.getId());
        Map<Long, LoyaltyAccount> accountByBusinessId = accounts.stream()
                .collect(Collectors.toMap(la -> la.getBusiness().getId(), la -> la));

        List<Business> activeBusinesses = businessService.getBusinessesByStatus(BusinessStatus.ACTIVE);

        Set<Long> businessIds = activeBusinesses.stream()
                .map(Business::getId)
                .collect(Collectors.toSet());

        Map<Long, LoyaltySettings> settingsByBusinessId = loyaltySettingsRepository
                .findAllByBusinessIdIn(businessIds)
                .stream()
                .collect(Collectors.toMap(s -> s.getBusiness().getId(), s -> s));

        return activeBusinesses.stream()
                .map(business -> toDto(business, accountByBusinessId, settingsByBusinessId))
                .toList();
    }

    private CustomerBusinessDto toDto(Business business,
                                      Map<Long, LoyaltyAccount> accountByBusinessId,
                                      Map<Long, LoyaltySettings> settingsByBusinessId) {
        LoyaltyAccount account = accountByBusinessId.get(business.getId());
        LoyaltySettings settings = settingsByBusinessId.get(business.getId());

        int points = account != null ? account.getAvailablePoints() : 0;
        int nextRewardPoints = settings != null ? settings.getMinPointsToRedeem() : 1;

        BusinessCategory category = business.getBusinessType();
        int categoryId = category != null ? category.ordinal() : 0;
        String categoryKey = category != null ? category.name() : "";
        String categoryLabel = category != null ? toTitleCase(category.name()) : "";

        return new CustomerBusinessDto(
                business.getId(),
                business.getBusinessName(),
                categoryId,
                categoryKey,
                categoryLabel,
                points,
                nextRewardPoints,
                null,
                business.getRating(),
                business.getLogoPath() != null,
                business.getLogoPath(),
                null,
                null,
                business.getAddress(),
                business.getBusinessPhoneNumber(),
                business.getBusinessEmail(),
                business.getBusinessDescription(),
                false,
                null
        );
    }

    private String toTitleCase(String name) {
        return Arrays.stream(name.split("_"))
                .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
