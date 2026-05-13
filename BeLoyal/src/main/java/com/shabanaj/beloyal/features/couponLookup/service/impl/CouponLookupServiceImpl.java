package com.shabanaj.beloyal.features.couponLookup.service.impl;

import com.shabanaj.beloyal.common.Exception.CatalogCategoryNotFound;
import com.shabanaj.beloyal.common.Exception.CatalogItemNotFound;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.couponLookup.dto.CategoryLookupItem;
import com.shabanaj.beloyal.features.couponLookup.dto.ProductLookupItem;
import com.shabanaj.beloyal.features.couponLookup.dto.VariantLookupItem;
import com.shabanaj.beloyal.features.couponLookup.service.CouponLookupService;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponLookupServiceImpl implements CouponLookupService {

    private final CatalogCategoryRepository catalogCategoryRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final CatalogItemVariantRepository catalogItemVariantRepository;

    @Override
    public List<CategoryLookupItem> getActiveCategories(Long businessId) {
        return catalogCategoryRepository
                .findAllByBusinessIdAndStatusAndIsDeletedFalseOrderByOrderIndexAsc(businessId, CatalogStatus.ACTIVE)
                .stream()
                .map(c -> CategoryLookupItem.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .status(c.getStatus())
                        .build())
                .toList();
    }

    @Override
    public List<ProductLookupItem> getActiveProductsByCategory(Long businessId, Long categoryId) {
        CatalogCategory category = catalogCategoryRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(categoryId, businessId)
                .orElseThrow(() -> new CatalogCategoryNotFound("Category not found: " + categoryId));

        if (category.getStatus() != CatalogStatus.ACTIVE) {
            return List.of();
        }

        return catalogItemRepository
                .findAllByBusinessIdAndCategoryIdAndIsDeletedFalseOrderByOrderIndexAsc(businessId, categoryId)
                .stream()
                .filter(p -> p.getStatus() == CatalogStatus.ACTIVE)
                .map(p -> ProductLookupItem.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .categoryId(p.getCategory().getId())
                        .imageUrl(p.getImageUrl())
                        .status(p.getStatus())
                        .build())
                .toList();
    }

    @Override
    public List<VariantLookupItem> getActiveVariantsByProduct(Long businessId, Long productId) {
        CatalogItem product = catalogItemRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(productId, businessId)
                .orElseThrow(() -> new CatalogItemNotFound("Product not found: " + productId));

        if (product.getStatus() != CatalogStatus.ACTIVE) {
            return List.of();
        }

        String currencyCode = product.getCurrencyCode().name();

        return catalogItemVariantRepository
                .findByCatalogItemIdAndIsDeletedFalseOrderByOrderIndexAsc(productId)
                .stream()
                .filter(v -> v.getStatus() == CatalogStatus.ACTIVE)
                .map(v -> {
                    java.math.BigDecimal price = v.getPriceOverride() != null
                            ? v.getPriceOverride()
                            : product.getPrice();
                    return VariantLookupItem.builder()
                            .id(v.getId())
                            .name(v.getName())
                            .status(v.getStatus())
                            .price(price)
                            .currency(currencyCode)
                            .build();
                })
                .toList();
    }
}
