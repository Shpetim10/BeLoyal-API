package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.PointsBucketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "points_buckets", uniqueConstraints = {
        @UniqueConstraint(name = "uk_points_buckets_loyalty_account_source_transaction", columnNames = {"loyalty_account_id","source_transaction_id"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsBucket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loyalty_account_id", nullable = false)
    private LoyaltyAccount loyaltyAccount;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_transaction_id", nullable = false, unique = true)
    private PointsTransaction sourceTransaction;

    @Column(name = "points_earned", nullable = false)
    @Min(0)
    private Integer pointsEarned;

    @Column(name = "points_remaining", nullable = false)
    @Min(0)
    private Integer pointsRemaining;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PointsBucketStatus status;

    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name="created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
