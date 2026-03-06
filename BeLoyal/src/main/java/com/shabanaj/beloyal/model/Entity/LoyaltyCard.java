package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.LoyaltyCardStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_cards")
@EnableJpaAuditing
@Getter
@Setter
public class LoyaltyCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_profile_id")
    private CustomerProfile customerProfile;

    @Column(name = "qr_token", unique = true, nullable = false)
    private String qrToken;

    @Column(name = "manual_code", unique = true, nullable = false)
    private String manualCode;

    @Column(name = "status")
    private LoyaltyCardStatus status;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name="created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
