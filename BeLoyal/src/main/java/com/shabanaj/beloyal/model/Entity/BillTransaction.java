package com.shabanaj.beloyal.model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_transactions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_bill_transaction_business_invoice", columnNames = {"business_id", "invoice_reference"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
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

    @Column(name = "invoice_reference", unique = true)
    private String invoiceReference;

    @Column(name = "note")
    private String note;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
