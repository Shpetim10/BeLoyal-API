package com.shabanaj.beloyal.model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_transactions", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_bill_tx_business_idem",
                columnNames = {"business_id", "idempotency_key"}
        )
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="business_member_id", nullable = false)
    private BusinessMember businessMember;

    @Column(name = "net_amount", nullable = false)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal netAmount;

    @Column(name="discount_amount", nullable = false)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal discountAmount;

    @Column(name="bill_amount", nullable = false)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal billAmount;

    @Column(name = "invoice_reference")
    private String invoiceReference;

    @Column(name = "note")
    private String note;

    @Column(name = "idempotency_key", nullable = false, updatable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "request_hash", nullable = false, updatable = false, length = 64)
    private String requestHash;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
