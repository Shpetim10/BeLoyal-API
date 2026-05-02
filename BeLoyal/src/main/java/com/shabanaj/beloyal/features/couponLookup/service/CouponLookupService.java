package com.shabanaj.beloyal.features.couponLookup.service;

import com.shabanaj.beloyal.features.couponLookup.dto.CategoryLookupItem;
import com.shabanaj.beloyal.features.couponLookup.dto.ProductLookupItem;
import com.shabanaj.beloyal.features.couponLookup.dto.VariantLookupItem;

import java.util.List;

public interface CouponLookupService {
    List<CategoryLookupItem> getActiveCategories(Long businessId);
    List<ProductLookupItem> getActiveProductsByCategory(Long businessId, Long categoryId);
    List<VariantLookupItem> getActiveVariantsByProduct(Long businessId, Long productId);
}
