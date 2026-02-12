package com.shabanaj.beloyal.Dto.Registration;

import com.shabanaj.beloyal.Enums.Gender;

import java.time.LocalDate;

public class CustomerProfileRegisterDto {
    private LocalDate birthdate;
    private Gender gender;
    private String city;
    private String country;
    private String referredBy;
    private boolean notificationEnabled;

    public CustomerProfileRegisterDto() {}

    public CustomerProfileRegisterDto(LocalDate birthdate, Gender gender, String city, String country, String referredBy, boolean notificationEnabled) {
        this.birthdate = birthdate;
        this.gender = gender;
        this.city = city;
        this.country = country;
        this.referredBy = referredBy;
        this.notificationEnabled = notificationEnabled;
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
}
