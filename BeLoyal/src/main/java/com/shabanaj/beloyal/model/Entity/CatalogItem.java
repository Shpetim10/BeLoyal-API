package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.CatalogItemType;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "catalog_item",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_order_index", columnNames = {"business_id", "category_id", "order_index"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="business_id",  nullable=false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CatalogCategory category;

    @Column(nullable = false)
    @Size(max = 120)
    private String name;

    @Column
    @Size(max = 500, message = "The item description should not exceed 500 characters")
    private String description;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    @DecimalMin(value = "0.1", inclusive = true)
    private BigDecimal price;

    @Column(name="type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CatalogItemType type;

    @Column(name="currency_code", nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;

    @Column(name="unit")
    private String unit;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private CatalogStatus status;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted=false;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
