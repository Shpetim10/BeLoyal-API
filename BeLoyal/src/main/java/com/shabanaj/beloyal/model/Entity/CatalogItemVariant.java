package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.CatalogStatus;
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
@Table(name = "catalog_item_variant", uniqueConstraints = {
        @UniqueConstraint(name = "uk_item_variant", columnNames = {"catalog_item_id", "name"})
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItemVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "catalog_item_id", nullable = false)
    private CatalogItem catalogItem;

    @Column(name = "name", nullable = false)
    @Size(max = 120)

    private String name;

    @Column(name = "description")
    @Size(max=500)
    private String description;

    @Column(name = "price_override", nullable = false , precision = 12, scale = 2)
    @DecimalMin(value = "0.1", inclusive = true)
    private BigDecimal priceOverride;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CatalogStatus status;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted=false;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
