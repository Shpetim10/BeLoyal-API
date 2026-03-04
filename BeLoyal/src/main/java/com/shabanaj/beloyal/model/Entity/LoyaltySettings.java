package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.ExpiryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_settings")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class LoyaltySettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id")
    private Business business;

    @Column(nullable = false, name = "min_points_to_redeem")
    @Min(1)
    private Integer minPointsToRedeem;

    @Column(name = "max_points_to_redeem")
    @Min(1)
    private Integer maxPointsToRedeem;

    @Column(nullable = false, name = "points_per_unit_discount")
    @Min(0)
    private Integer pointsPerUnitDiscount;

    @Column(name = "max_points_per_transaction")
    @Min(0)
    private Integer maxPointsPerTransactions;

    @Column(name = "expiry_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExpiryType expiryType;

    @Column(name = "months_to_expire")
    @Min(1)
    private Integer monthsToExpire;

    @Column(name="enabled")
    private boolean enabled;

    @Column(name = "configured")
    private boolean configured;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
