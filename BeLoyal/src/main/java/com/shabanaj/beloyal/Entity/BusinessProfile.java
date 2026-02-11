package com.shabanaj.beloyal.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.shabanaj.beloyal.Enums.BusinessType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="business_profiles")
@EntityListeners(AuditingEntityListener.class)
public class BusinessProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String businessName;

    @Column(name = "business_type", nullable = false)
    private BusinessType businessType;

    @Column(name = "business_description", length = 1000)
    private String businessDescription;

    private String logoUrl;
    private String address;
    private String city;
    private String country;
    private String websiteUrl;
    @Column(name = "vat_id", nullable = false, unique= true)
    private String vatId;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "5.0", inclusive = true)
    private Double rating=0.0;

    @Column(name = "business_phone_number")
    private String businessPhoneNumber;
    @Email
    @Column(name = "business_email", unique = true)
    private String businessEmail;

    private boolean verified = false; // approved by super admin
    private boolean active = true;    // for suspending temporarily

    @OneToMany(mappedBy = "businessProfile", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WaiterProfile> waiterProfiles = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public BusinessProfile(){}

    public BusinessProfile(Long id, User user, String businessName, BusinessType businessType, String businessDescription, String logoUrl, String address, String city, String country, String websiteUrl, String vatId, Double rating, String businessPhoneNumber, String businessEmail) {
        this.id = id;
        this.user = user;
        this.businessName = businessName;
        this.businessType = businessType;
        this.businessDescription = businessDescription;
        this.logoUrl = logoUrl;
        this.address = address;
        this.city = city;
        this.country = country;
        this.websiteUrl = websiteUrl;
        this.vatId = vatId;
        this.rating = rating;
        this.businessPhoneNumber = businessPhoneNumber;
        this.businessEmail = businessEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public BusinessProfile(User user, String businessName, BusinessType businessType, String businessDescription, String logoUrl, String address, String city, String country, String websiteUrl, String vatId, Double rating, String businessPhoneNumber, String businessEmail) {
        this.user = user;
        this.businessName = businessName;
        this.businessType = businessType;
        this.businessDescription = businessDescription;
        this.logoUrl = logoUrl;
        this.address = address;
        this.city = city;
        this.country = country;
        this.websiteUrl = websiteUrl;
        this.vatId = vatId;
        this.rating = rating;
        this.businessPhoneNumber = businessPhoneNumber;
        this.businessEmail = businessEmail;
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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    public void setBusinessPhoneNumber(String businessPhoneNumber) {
        this.businessPhoneNumber = businessPhoneNumber;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
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
