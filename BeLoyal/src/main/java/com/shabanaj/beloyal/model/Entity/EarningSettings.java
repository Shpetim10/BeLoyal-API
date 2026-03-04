package com.shabanaj.beloyal.model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "earning_settings")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class EarningSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false, unique = true)
    private Business business;

    @Column(nullable = false, name = "points_per")
    @Min(0)
    private Integer pointsPer;

    @Column(nullable = false, name = "amount_per")
    @Min(1)
    private Integer amountPer;

    private boolean enabled=false;
    private boolean configured=false;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
