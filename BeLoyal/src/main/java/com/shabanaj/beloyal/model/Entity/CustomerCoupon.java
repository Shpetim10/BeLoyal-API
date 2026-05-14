package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import com.shabanaj.beloyal.model.Enums.RedemptionChannel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_coupons")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coupon_id", nullable = false)
    private LoyaltyCoupon coupon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_profile_id", nullable = false)
    private CustomerProfile customerProfile;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerCouponStatus status;

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "order_id", length = 100)
    private String orderId;

    @Column(name = "points_spent", nullable = false)
    private int pointsSpent;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyCode currency;

    // Snapshot fields
    @Column(name = "snapshot_title", length = 200)
    private String snapshotTitle;

    @Column(name = "snapshot_description", length = 1000)
    private String snapshotDescription;

    @Column(name = "snapshot_image_url", length = 500)
    private String snapshotImageUrl;

    @Column(name = "snapshot_coupon_type", length = 30)
    @Enumerated(EnumType.STRING)
    private CouponType snapshotCouponType;

    @Column(name = "snapshot_product_id")
    private Long snapshotProductId;

    @Column(name = "snapshot_variant_id")
    private Long snapshotVariantId;

    @Column(name = "snapshot_discount_percentage", precision = 5, scale = 2)
    private BigDecimal snapshotDiscountPercentage;

    @Column(name = "snapshot_discount_amount", precision = 12, scale = 2)
    private BigDecimal snapshotDiscountAmount;

    @Column(name = "snapshot_minimum_order_amount", precision = 12, scale = 2)
    private BigDecimal snapshotMinimumOrderAmount;

    @Column(name = "snapshot_maximum_discount_amount", precision = 12, scale = 2)
    private BigDecimal snapshotMaximumDiscountAmount;

    @Column(name = "qr_code", unique = true, length = 255)
    private String qrCode;

    @Column(name = "qr_code_generated_at")
    private LocalDateTime qrCodeGeneratedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redeemed_by_staff_id")
    private BusinessMember redeemedByStaff;

    @Column(name = "redemption_location", length = 255)
    private String redemptionLocation;

    @Column(name = "redemption_channel", length = 50)
    @Enumerated(EnumType.STRING)
    private RedemptionChannel redemptionChannel;

    @Column(name = "redemption_transaction_id")
    private Long redemptionTransactionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
