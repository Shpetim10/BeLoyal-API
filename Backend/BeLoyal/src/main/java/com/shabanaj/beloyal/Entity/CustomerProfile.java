package com.shabanaj.beloyal.Entity;

import com.shabanaj.beloyal.Enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="customer_profiles")
@EntityListeners(AuditingEntityListener.class)
public class CustomerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable= false, unique = true)
    private User user;

    //here will be added loyalty points fields

    @Column(name = "referral_code", nullable = false, unique = true, updatable = false)
    @Size(max = 20)
    private String referralCode;

    @Column(name = "referred_by", updatable = false)
    @Size(max = 20)
    private String referredBy;

    @Past
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String country;

    private boolean notificationEnabled=true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public CustomerProfile(){}

    public CustomerProfile(Long id, User user, String referralCode, String referredBy, LocalDate birthDate, Gender gender, String city, String country, boolean notificationEnabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.referralCode = referralCode;
        this.referredBy = referredBy;
        this.birthDate = birthDate;
        this.gender = gender;
        this.city = city;
        this.country = country;
        this.notificationEnabled = notificationEnabled;
    }

    public CustomerProfile(Long id, User user, String referralCode, String referredBy, LocalDate birthDate, Gender gender, String city, String country, boolean notificationEnabled) {
        this.id = id;
        this.user = user;
        this.referralCode = referralCode;
        this.referredBy = referredBy;
        this.birthDate = birthDate;
        this.gender = gender;
        this.city = city;
        this.country = country;
        this.notificationEnabled = notificationEnabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
