package com.shabanaj.beloyal.Dto;

import com.shabanaj.beloyal.Enums.Gender;

import java.time.LocalDate;

public class CustomerProfileRegisterDto {
    private LocalDate birthdate;
    private Gender gender;
    private String city;
    private String country;
    private boolean notificationEnabled;

    public CustomerProfileRegisterDto(LocalDate birthdate, Gender gender, String city, String country, boolean notificationEnabled) {
        this.birthdate = birthdate;
        this.gender = gender;
        this.city = city;
        this.country = country;
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

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }
}
