package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.PointsType;
import com.shabanaj.beloyal.model.Enums.ScanMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_transactions")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class PointsTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loyalty_account_id", nullable = false)
    private LoyaltyAccount loyaltyAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_transaction_id")
    private BillTransaction billTransaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by_member_id", nullable = false)
    private BusinessMember businessMember;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PointsType type;

    @Column(name = "description")
    private String description;

    @Column(name = "reason")
    private String reason;

    @Column(name = "scan_method")
    @Enumerated(EnumType.STRING)
    private ScanMethod scanMethod;

    @Column(name = "money_amount")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal moneyAmount;

    @Column(name = "discount_amount")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal discountAmount;

    @Column(name = "points_delta", nullable=false)
    @NotNull
    private Integer pointsDelta;

    @Column(name = "rule_amount_per")
    private Integer ruleAmountPer;

    @Column(name = "rule_points_per")
    private Integer rulePointsPer;

    @Version
    private Long version;

    @Column(name="created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
