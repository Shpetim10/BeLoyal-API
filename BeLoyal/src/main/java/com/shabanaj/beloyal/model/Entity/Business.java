package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import com.shabanaj.beloyal.model.Enums.BusinessType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Clock;
import java.time.LocalDateTime;

@Entity
@Table(name = "business")
@EntityListeners(AuditingEntityListener.class)
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private BusinessType businessType;

    @Size(max = 1000)
    @Column(name = "business_description", length = 1000)
    private String businessDescription;

    private String logoPath;

    private String address;

    @Column(nullable = false)
    private String city;

    private String country;

    private String websiteUrl;

    @Column(name = "vat_id", length = 60, nullable = false, unique = true)
    private String vatId;

    @Column(nullable = false)
    private Double rating = 0.0;

    @Column(name = "business_phone_number")
    private String businessPhoneNumber;

    @Email
    @Column(name = "business_email")
    private String businessEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessStatus businessStatus = BusinessStatus.PENDING_APPROVAL;

    @Column
    private LocalDateTime submittedAt;

    @Column
    private LocalDateTime reviewedAt;

    @Column(length=2000)
    private String rejectionReason;

    @Column
    private Long reviewedByAdminId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Business() {}

    // Domain helpers
    public void activate(Long adminId, Clock clock) {
        this.businessStatus = BusinessStatus.ACTIVE;
        this.reviewedAt = LocalDateTime.now(clock);
        this.reviewedByAdminId = adminId;
    }

    public void reject(Long adminId, Clock clock, String reason) {
        this.businessStatus = BusinessStatus.REJECTED;
        this.reviewedAt = LocalDateTime.now(clock);
        this.reviewedByAdminId = adminId;
        this.rejectionReason = reason;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public BusinessType getBusinessType() { return businessType; }
    public void setBusinessType(BusinessType businessType) { this.businessType = businessType; }

    public String getBusinessDescription() { return businessDescription; }
    public void setBusinessDescription(String businessDescription) { this.businessDescription = businessDescription; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getVatId() { return vatId; }
    public void setVatId(String vatId) { this.vatId = vatId; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getBusinessPhoneNumber() { return businessPhoneNumber; }
    public void setBusinessPhoneNumber(String businessPhoneNumber) { this.businessPhoneNumber = businessPhoneNumber; }

    public String getBusinessEmail() { return businessEmail; }
    public void setBusinessEmail(String businessEmail) { this.businessEmail = businessEmail; }

    public BusinessStatus getBusinessStatus() { return businessStatus; }
    public void setBusinessStatus(BusinessStatus businessStatus) { this.businessStatus = businessStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Long getReviewedByAdminId() {
        return reviewedByAdminId;
    }

    public void setReviewedByAdminId(Long reviewedByAdminId) {
        this.reviewedByAdminId = reviewedByAdminId;
    }
}
