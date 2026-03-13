package com.shabanaj.beloyal.model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "points_bucket_consumption", uniqueConstraints = {
        @UniqueConstraint(name = "uk_points_bucket_consumption_points_transaction_points_bucket", columnNames = {"points_transaction_id", "points_bucket_id"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class PointsBucketConsumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name= "points_transaction_id", nullable = false)
    private PointsTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "points_bucket_id", nullable = false)
    private PointsBucket pointsBucket;

    @Column(name = "points_used", nullable = false)
    @Min(1)
    private Integer pointsUsed;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;
}
