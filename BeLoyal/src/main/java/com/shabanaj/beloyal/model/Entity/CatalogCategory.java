package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name="catalog_category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_business_category_name", columnNames = {"business_id", "name"})
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "name", nullable = false)
    @Size(max = 120, message = "The name of category is too long")
    private String name;

    @Column(name = "description")
    @Size(max = 300, message = "The category description should not exceed 300 characters")
    private String description;

    @Column(name="order_index", nullable = false)
    private Integer orderIndex;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CatalogStatus status;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    // helpers
    public void activate(){
        this.status = CatalogStatus.ACTIVE;
    }

    public void deactivate(){
        this.status = CatalogStatus.INACTIVE;
    }
}
