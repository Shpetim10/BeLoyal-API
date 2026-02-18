package com.shabanaj.beloyal.registration.dto.customerRegistraton;

import com.shabanaj.beloyal.model.Enums.Gender;

import java.time.LocalDate;

public class CustomerProfileRegisterDto {
    private LocalDate birthdate;
    private Gender gender;
    private String city;
    private String country;
    private String referredBy;
    private String profileImagePath;
    private boolean notificationEnabled;

    public CustomerProfileRegisterDto() {}

    public CustomerProfileRegisterDto(LocalDate birthdate, Gender gender, String city, String country, String referredBy, boolean notificationEnabled , String profileImagePath) {
        this.birthdate = birthdate;
        this.gender = gender;
        this.city = city;
        this.country = country;
        this.referredBy = referredBy;
        this.notificationEnabled = notificationEnabled;
        this.profileImagePath = profileImagePath;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}
