package com.shabanaj.beloyal.model.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon_free_product_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponFreeProductDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coupon_id", nullable = false, unique = true)
    private LoyaltyCoupon coupon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CatalogCategory category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private CatalogItem product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private CatalogItemVariant variant;

    @Builder.Default
    @Column(name = "quantity", nullable = false)
    private int quantity = 1;
}
