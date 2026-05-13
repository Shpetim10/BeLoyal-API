package com.shabanaj.beloyal.features.userProfiles.customer.dto;

import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Gender;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CustomerProfileDto {

    private final Long id;
    private final UserSummary user;
    private final String referralCode;
    private final String referredBy;
    private final LocalDate birthDate;
    private final Gender gender;
    private final String city;
    private final String country;
    private final boolean notificationEnabled;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CustomerProfileDto(CustomerProfile profile) {
        User u = profile.getUser();
        this.id = profile.getId();
        this.user = new UserSummary(u.getId(), u.getFirstName(), u.getLastName(),
                u.getEmail(), u.getUsername(), u.getPhoneNumber(), u.getProfileImage());
        this.referralCode = profile.getReferralCode();
        this.referredBy = profile.getReferredBy();
        this.birthDate = profile.getBirthDate();
        this.gender = profile.getGender();
        this.city = profile.getCity();
        this.country = profile.getCountry();
        this.notificationEnabled = profile.isNotificationEnabled();
        this.createdAt = profile.getCreatedAt();
        this.updatedAt = profile.getUpdatedAt();
    }

    public record UserSummary(
            Long id,
            String firstName,
            String lastName,
            String email,
            String username,
            String phoneNumber,
            String profileImageUrl
    ) {}
}
