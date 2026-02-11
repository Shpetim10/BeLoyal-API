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
@Table(name = "customer_profiles")
@EntityListeners(AuditingEntityListener.class)
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User (0..1) <-> (1) CustomerProfile
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "referral_code", nullable = false, unique = true, updatable = false, length = 20)
    private String referralCode;

    @Column(name = "referred_by", updatable = false, length = 20)
    private String referredBy;

    // Customer-only optional fields
    @Past
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String country;

    @Column(nullable = false)
    private boolean notificationEnabled = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public CustomerProfile() {}

    public CustomerProfile(User user, String referralCode, String referredBy) {
        this.user = user;
        this.referralCode = referralCode;
        this.referredBy = referredBy;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public String getReferredBy() { return referredBy; }
    public void setReferredBy(String referredBy) { this.referredBy = referredBy; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public boolean isNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
