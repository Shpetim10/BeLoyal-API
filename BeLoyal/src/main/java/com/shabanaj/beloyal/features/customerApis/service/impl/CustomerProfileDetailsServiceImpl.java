package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerProfileDetailsResponse;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerProfileHeaderDto;
import com.shabanaj.beloyal.features.customerApis.dto.CustomerSummaryDto;
import com.shabanaj.beloyal.features.customerApis.service.CustomerProfileDetailsService;
import com.shabanaj.beloyal.features.customerApis.service.CustomerStatsService;
import com.shabanaj.beloyal.features.loyaltyCard.service.LoyaltyCardService;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import com.shabanaj.beloyal.model.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.TextStyle;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomerProfileDetailsServiceImpl implements CustomerProfileDetailsService {

    private final UserService userService;
    private final CustomerProfileService customerProfileService;
    private final LoyaltyCardService loyaltyCardService;
    private final CustomerStatsService customerStatsService;

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileDetailsResponse getProfileDetails(Long userId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);
        LoyaltyCard loyaltyCard = loyaltyCardService.getLoyaltyCardForUser(user);

        CustomerProfileHeaderDto profile = buildHeader(user, customerProfile, loyaltyCard);
        CustomerSummaryDto stats = customerStatsService.getCustomerStats(userId);

        return new CustomerProfileDetailsResponse(profile, stats);
    }

    private CustomerProfileHeaderDto buildHeader(User user, CustomerProfile customerProfile, LoyaltyCard loyaltyCard) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String fullName = (firstName + " " + lastName).trim();
        String avatarInitials = buildInitials(firstName, lastName);
        String memberSince = customerProfile.getCreatedAt()
                .getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + customerProfile.getCreatedAt().getYear();

        return new CustomerProfileHeaderDto(
                firstName,
                lastName,
                fullName,
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfileImage(),
                avatarInitials,
                user.getStatus().name(),
                memberSince,
                loyaltyCard.getManualCode(),
                customerProfile.getBirthDate(),
                customerProfile.getGender(),
                customerProfile.getCity(),
                customerProfile.getCountry(),
                customerProfile.getReferralCode(),
                customerProfile.getReferredBy(),
                customerProfile.isNotificationEnabled(),
                user.getAcceptedTcVersion() != null
        );
    }

    private String buildInitials(String firstName, String lastName) {
        StringBuilder initials = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            initials.append(Character.toUpperCase(firstName.charAt(0)));
        }
        if (lastName != null && !lastName.isBlank()) {
            initials.append(Character.toUpperCase(lastName.charAt(0)));
        }
        return initials.isEmpty() ? "?" : initials.toString();
    }
}
