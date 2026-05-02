package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CouponVisibility;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_coupons")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false, updatable = false)
    private Business business;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CouponType type;

    @Column(name = "title", nullable = false, length = 200)
    @NotNull
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "points_cost", nullable = false)
    @Min(1)
    private int pointsCost;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CurrencyCode currency;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CouponStatus status = CouponStatus.DRAFT;

    @Column(name = "visibility", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CouponVisibility visibility = CouponVisibility.PUBLIC;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "total_redemption_limit")
    private Integer totalRedemptionLimit;

    @Builder.Default
    @Column(name = "total_redemptions", nullable = false)
    private int totalRedemptions = 0;

    @Column(name = "per_customer_redemption_limit")
    private Integer perCustomerRedemptionLimit;

    @Column(name = "terms_and_conditions", length = 2000)
    private String termsAndConditions;

    @Builder.Default
    @Column(name = "is_featured", nullable = false)
    private boolean isFeatured = false;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Version
    @Builder.Default
    @Column(name = "version", nullable = false)
    private long version = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "coupon", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private CouponFreeProductDetails freeProductDetails;

    @OneToOne(mappedBy = "coupon", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private CouponDiscountDetails discountDetails;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isActive() {
        return status == CouponStatus.ACTIVE;
    }

    public boolean isWithinDateRange(LocalDateTime now) {
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    public boolean isSoldOut() {
        return totalRedemptionLimit != null && totalRedemptions >= totalRedemptionLimit;
    }
}
