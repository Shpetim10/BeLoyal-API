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
@Table(
        name = "loyalty_accounts",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_loyalty_account_unique_profile_and_business", columnNames = {"customer_profile_id" , "business_id"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class LoyaltyAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_profile_id",  nullable = false, updatable = false)
    private CustomerProfile customerProfile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="business_id", nullable = false, updatable = false)
    private Business business;

    @Column(name="available_points")
    @Min(0)
    private int availablePoints=0;

    @Column(name= "lifetime_earned")
    @Min(0)
    private int lifetimeEarned=0;

    @Column(name="lifetime_redeemed")
    @Min(0)
    private int lifetimeRedeemed=0;

    @Column(name="lifetime_expired")
    @Min(0)
    private int lifetimeExpired=0;

    @Column(name="last_activity_at")
    private  LocalDateTime lastActivityAt;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    // helpers
    public void add(Integer points){
        this.availablePoints+=points;
        this.lifetimeEarned+=points;
    }

    public void spend(Integer points){
        this.availablePoints-=points;
        this.lifetimeRedeemed+=points;
    }

    public void expire(Integer points){
        this.availablePoints-=points;
        this.lifetimeExpired+=points;
    }
}
