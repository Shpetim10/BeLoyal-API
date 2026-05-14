package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerApis.dto.*;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.customerApis.service.CustomerBusinessDetailService;
import com.shabanaj.beloyal.features.earningSettings.repository.EarningSettingsRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.loyaltyCard.service.LoyaltyCardService;
import com.shabanaj.beloyal.features.loyaltySettings.repository.LoyaltySettingsRepository;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.registerLoyaltyPoints.service.PointsCalculatorService;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.*;
import com.shabanaj.beloyal.model.Enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerBusinessDetailServiceImpl implements CustomerBusinessDetailService {

    private final UserService userService;
    private final CustomerProfileService customerProfileService;
    private final LoyaltyCardService loyaltyCardService;
    private final BusinessService businessService;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final LoyaltySettingsRepository loyaltySettingsRepository;
    private final EarningSettingsRepository earningSettingsRepository;
    private final CatalogCategoryRepository catalogCategoryRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final CatalogItemVariantRepository catalogItemVariantRepository;
    private final CustomerCouponRepository customerCouponRepository;
    private final CouponRepository couponRepository;
    private final PointsTransactionRepository pointsTransactionRepository;
    private final PointsCalculatorService pointsCalculatorService;

    @Override
    @Transactional(readOnly = true)
    public CustomerBusinessDetailResponse getBusinessDetail(Long userId, Long businessId) {
        User user = userService.getUserOrThrow(userId);
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileByUser(user);
        LoyaltyCard loyaltyCard = loyaltyCardService.getLoyaltyCardForUser(user);

        Business business = businessService.getActiveBusinessById(businessId);

        LoyaltyAccount loyaltyAccount = loyaltyAccountRepository
                .findByCustomerProfileAndBusiness(customerProfile, business)
                .orElse(null);

        LoyaltySettings loyaltySettings = loyaltySettingsRepository
                .findByBusinessId(businessId)
                .orElse(null);

        EarningSettings earningSettings = earningSettingsRepository
                .findByBusinessId(businessId)
                .orElse(null);

        LocalDateTime now = LocalDateTime.now();
        List<LoyaltyCoupon> availablePublicCoupons = couponRepository.findAvailableForBusiness(businessId, now);

        return new CustomerBusinessDetailResponse(
                buildBusinessDetail(business),
                buildLoyalty(loyaltyAccount, loyaltySettings, earningSettings, loyaltyCard, availablePublicCoupons, business, customerProfile),
                buildCatalog(business, earningSettings),
                buildCoupons(customerProfile, business, availablePublicCoupons),
                buildTransactions(userId, businessId),
                buildDetails(business)
        );
    }

    private CustomerBusinessDetailDto buildBusinessDetail(Business business) {
        BusinessCategory category = business.getBusinessType();
        int categoryId = category != null ? category.ordinal() : 0;
        String categoryKey = category != null ? category.name() : "";
        String categoryLabel = category != null ? toTitleCase(category.name()) : "";

        BusinessLocationDto location = new BusinessLocationDto(
                business.getAddress(),
                null,
                business.getCity(),
                business.getCountry(),
                null,
                null,
                null,
                buildMapLabel(business)
        );

        return new CustomerBusinessDetailDto(
                business.getId(),
                business.getBusinessName(),
                categoryId,
                categoryKey,
                categoryLabel,
                business.getRating(),
                business.getLogoPath() != null,
                business.getLogoPath(),
                business.getBusinessDescription(),
                business.getAddress(),
                business.getBusinessPhoneNumber(),
                business.getBusinessEmail(),
                business.getWebsiteUrl(),
                location
        );
    }

    private CustomerLoyaltyDto buildLoyalty(LoyaltyAccount account,
                                             LoyaltySettings loyaltySettings,
                                             EarningSettings earningSettings,
                                             LoyaltyCard loyaltyCard,
                                             List<LoyaltyCoupon> availablePublicCoupons,
                                             Business business,
                                             CustomerProfile customerProfile) {
        int currentPoints = account != null ? account.getAvailablePoints() : 0;

        // Filter out coupons where this customer has exhausted their per-customer quota
        List<LoyaltyCoupon> redeemableCoupons = availablePublicCoupons.stream()
                .filter(c -> {
                    if (c.getPerCustomerRedemptionLimit() == null) return true;
                    int used = customerCouponRepository.countByCouponIdAndCustomerProfileId(
                            c.getId(), customerProfile.getId());
                    return used < c.getPerCustomerRedemptionLimit();
                })
                .toList();

        int nextRewardPoints = redeemableCoupons.stream()
                .mapToInt(LoyaltyCoupon::getPointsCost)
                .min()
                .orElseGet(() -> loyaltySettings != null && loyaltySettings.isConfigured()
                        ? Math.max(1, loyaltySettings.getMinPointsToRedeem())
                        : 1);

        int pointsToNextReward = Math.max(0, nextRewardPoints - currentPoints);

        LoyaltySettingsSummaryDto loyaltySettingsDto = loyaltySettings != null
                ? new LoyaltySettingsSummaryDto(
                        loyaltySettings.getMinPointsToRedeem(),
                        loyaltySettings.getMaxPointsToRedeem(),
                        loyaltySettings.getPointsPerUnitDiscount(),
                        loyaltySettings.getMaxPointsPerTransactions(),
                        loyaltySettings.getExpiryType() != null ? loyaltySettings.getExpiryType().name() : null,
                        loyaltySettings.getMonthsToExpire(),
                        loyaltySettings.isEnabled(),
                        loyaltySettings.isConfigured())
                : null;

        EarningSettingsSummaryDto earningSettingsDto = earningSettings != null
                ? new EarningSettingsSummaryDto(
                        earningSettings.getPointsPer(),
                        earningSettings.getAmountPer(),
                        earningSettings.isEnabled(),
                        earningSettings.isConfigured())
                : null;

        String loyaltyPolicy = deriveLoyaltyPolicy(loyaltySettings, earningSettings,
                business.getCurrencyCode() != null ? business.getCurrencyCode().getSymbol() : null);

        return new CustomerLoyaltyDto(
                currentPoints,
                nextRewardPoints,
                pointsToNextReward,
                loyaltyCard.getManualCode(),
                loyaltyPolicy,
                loyaltySettingsDto,
                earningSettingsDto,
                account != null ? account.getLifetimeEarned() : 0,
                account != null ? account.getLifetimeRedeemed() : 0,
                account != null ? account.getLifetimeExpired() : 0,
                account != null ? account.getLastActivityAt() : null
        );
    }

    private CustomerCatalogDto buildCatalog(Business business, EarningSettings earningSettings) {
        Long businessId = business.getId();

        List<CatalogCategory> categories = catalogCategoryRepository
                .findAllByBusinessIdAndStatusAndIsDeletedFalseOrderByOrderIndexAsc(businessId, CatalogStatus.ACTIVE);

        List<CatalogItem> items = catalogItemRepository
                .findAllByBusinessIdAndIsDeletedFalse(businessId)
                .stream()
                .filter(i -> i.getStatus() == CatalogStatus.ACTIVE)
                .toList();

        Set<Long> itemIds = items.stream().map(CatalogItem::getId).collect(Collectors.toSet());

        List<CatalogItemVariant> variants = itemIds.isEmpty()
                ? List.of()
                : catalogItemVariantRepository.findByCatalogItemIdInAndIsDeletedFalseOrderByOrderIndexAsc(itemIds);

        String pointsLabel = buildPointsLabel(earningSettings);

        List<CustomerCatalogCategoryDto> categoryDtos = categories.stream()
                .map(c -> new CustomerCatalogCategoryDto(
                        c.getId(),
                        c.getName(),
                        c.getDescription(),
                        c.getOrderIndex() != null ? c.getOrderIndex() : 0))
                .toList();

        List<CustomerCatalogItemDto> itemDtos = items.stream()
                .map(i -> new CustomerCatalogItemDto(
                        i.getId(),
                        i.getCategory().getId(),
                        i.getName(),
                        i.getDescription(),
                        i.getImageUrl(),
                        i.getStatus() == CatalogStatus.ACTIVE,
                        i.getOrderIndex() != null ? i.getOrderIndex() : 0,
                        pointsLabel,
                        i.getPrice(),
                        business.getCurrencyCode() != null ? i.getCurrencyCode().getSymbol() : null,
                        i.getUnit()))
                .toList();

        Map<Long, Long> firstVariantByItem = new LinkedHashMap<>();
        for (CatalogItemVariant v : variants) {
            firstVariantByItem.putIfAbsent(v.getCatalogItem().getId(), v.getId());
        }

        List<CustomerCatalogVariantDto> variantDtos = variants.stream()
                .map(v -> {
                    CatalogItem parentItem = v.getCatalogItem();
                    BigDecimal price = v.getPriceOverride() != null ? v.getPriceOverride() : parentItem.getPrice();
                    boolean isDefault = v.getId().equals(firstVariantByItem.get(parentItem.getId()));
                    return new CustomerCatalogVariantDto(
                            v.getId(),
                            parentItem.getId(),
                            v.getName(),
                            v.getDescription(),
                            price,
                            business.getCurrencyCode() != null ? business.getCurrencyCode().getSymbol() : null,
                            isDefault,
                            v.getStatus() == CatalogStatus.ACTIVE,
                            calculateEarnedPoints(price, earningSettings));
                })
                .toList();

        return new CustomerCatalogDto(categoryDtos, itemDtos, variantDtos);
    }

    private List<CustomerBusinessCouponDto> buildCoupons(CustomerProfile customerProfile, Business business,
                                                          List<LoyaltyCoupon> availablePublicCoupons) {
        List<CustomerCoupon> owned = customerCouponRepository
                .findAllByCustomerProfileIdAndBusinessIdOrderByCreatedAtDesc(
                        customerProfile.getId(), business.getId());

        Set<Long> ownedCouponIds = owned.stream()
                .map(cc -> cc.getCoupon().getId())
                .collect(Collectors.toSet());

        List<LoyaltyCoupon> publicCoupons = availablePublicCoupons.stream()
                .filter(c -> !ownedCouponIds.contains(c.getId()))
                .toList();

        List<CustomerBusinessCouponDto> result = new ArrayList<>();
        owned.stream().map(cc -> toOwnedCouponDto(cc, business)).forEach(result::add);
        publicCoupons.stream().map(c -> toPublicCouponDto(c, business)).forEach(result::add);
        return result;
    }

    private CustomerBusinessCouponDto toOwnedCouponDto(CustomerCoupon cc, Business business) {
        LoyaltyCoupon coupon = cc.getCoupon();
        String status = deriveStatus(cc);
        String discountDisplay = buildDiscountDisplay(coupon, cc.getSnapshotDiscountPercentage(), cc.getSnapshotDiscountAmount(), cc.getCurrency());
        BigDecimal discountValue = resolveDiscountValue(coupon, cc.getSnapshotDiscountPercentage(), cc.getSnapshotDiscountAmount());

        CouponFreeProductDetails fpd = coupon.getFreeProductDetails();

        return new CustomerBusinessCouponDto(
                cc.getId(),
                coupon.getId(),
                business.getId(),
                business.getBusinessName(),
                cc.getSnapshotTitle(),
                cc.getSnapshotDescription(),
                cc.getSnapshotImageUrl() != null ? cc.getSnapshotImageUrl() : coupon.getImageUrl(),
                mapPromotionType(coupon.getType()),
                status,
                discountDisplay,
                discountValue,
                cc.getPointsSpent(),
                cc.getCurrency() != null ? cc.getCurrency().getSymbol() : coupon.getCurrency().getSymbol(),
                cc.getStatus() == CustomerCouponStatus.USED,
                true,
                cc.getStatus() == CustomerCouponStatus.USED ? 1 : 0,
                coupon.getPerCustomerRedemptionLimit(),
                coupon.isFeatured(),
                coupon.getTotalRedemptions(),
                coupon.getTotalRedemptionLimit(),
                coupon.getStartDate(),
                cc.getExpiresAt(),
                coupon.getTermsAndConditions(),
                // Discount rule — prefer snapshot fields
                cc.getSnapshotMinimumOrderAmount(),
                cc.getSnapshotMaximumDiscountAmount(),
                // Free-product details
                fpd != null ? fpd.getCategory().getId() : null,
                fpd != null ? fpd.getCategory().getName() : null,
                fpd != null ? fpd.getProduct().getId() : null,
                fpd != null ? fpd.getProduct().getName() : null,
                fpd != null && fpd.getVariant() != null ? fpd.getVariant().getId() : null,
                fpd != null && fpd.getVariant() != null ? fpd.getVariant().getName() : null,
                fpd != null ? fpd.getQuantity() : null,
                // Snapshot overrides
                cc.getSnapshotTitle(),
                cc.getSnapshotDescription(),
                cc.getSnapshotImageUrl(),
                cc.getSnapshotCouponType() != null ? cc.getSnapshotCouponType().name() : null,
                cc.getSnapshotMinimumOrderAmount(),
                cc.getSnapshotMaximumDiscountAmount(),
                // Lifecycle timestamps
                cc.getRedeemedAt(),
                cc.getUsedAt(),
                cc.getOrderId(),
                cc.getQrCode()
        );
    }

    private CustomerBusinessCouponDto toPublicCouponDto(LoyaltyCoupon coupon, Business business) {
        CouponDiscountDetails details = coupon.getDiscountDetails();
        BigDecimal pct = details != null ? details.getDiscountPercentage() : null;
        BigDecimal amt = details != null ? details.getDiscountAmount() : null;
        String discountDisplay = buildDiscountDisplay(coupon, pct, amt, coupon.getCurrency());
        BigDecimal discountValue = resolveDiscountValue(coupon, pct, amt);

        CouponFreeProductDetails fpd = coupon.getFreeProductDetails();

        return new CustomerBusinessCouponDto(
                null,
                coupon.getId(),
                business.getId(),
                business.getBusinessName(),
                coupon.getTitle(),
                coupon.getDescription(),
                coupon.getImageUrl(),
                mapPromotionType(coupon.getType()),
                "ACTIVE",
                discountDisplay,
                discountValue,
                coupon.getPointsCost(),
                coupon.getCurrency().getSymbol(),
                false,
                false,
                0,
                coupon.getPerCustomerRedemptionLimit(),
                coupon.isFeatured(),
                coupon.getTotalRedemptions(),
                coupon.getTotalRedemptionLimit(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getTermsAndConditions(),
                // Discount rule details
                details != null ? details.getMinimumOrderAmount() : null,
                details != null ? details.getMaximumDiscountAmount() : null,
                // Free-product details
                fpd != null ? fpd.getCategory().getId() : null,
                fpd != null ? fpd.getCategory().getName() : null,
                fpd != null ? fpd.getProduct().getId() : null,
                fpd != null ? fpd.getProduct().getName() : null,
                fpd != null && fpd.getVariant() != null ? fpd.getVariant().getId() : null,
                fpd != null && fpd.getVariant() != null ? fpd.getVariant().getName() : null,
                fpd != null ? fpd.getQuantity() : null,
                // No snapshot fields for public/unowned coupons
                null, null, null, null, null, null,
                // No lifecycle timestamps for public/unowned coupons
                null, null, null, null
        );
    }

    private List<CustomerBusinessTransactionDto> buildTransactions(Long userId, Long businessId) {
        return pointsTransactionRepository
                .findPageByUserIdAndBusinessId(userId, businessId, PageRequest.of(0, 200))
                .getContent()
                .stream()
                .map(this::toTransactionDto)
                .toList();
    }

    private CustomerBusinessTransactionDto toTransactionDto(PointsTransaction pt) {
        BillTransaction bill = pt.getBillTransaction();
        return new CustomerBusinessTransactionDto(
                pt.getId(),
                pt.getLoyaltyAccount().getBusiness().getId(),
                pt.getLoyaltyAccount().getBusiness().getBusinessName(),
                mapTransactionType(pt.getType()),
                pt.getPointsDelta(),
                pt.getCreatedAt(),
                pt.getDescription(),
                bill != null ? bill.getNetAmount() : null,
                bill != null ? bill.getBillAmount() : null,
                bill != null ? bill.getDiscountAmount() : null,
                bill != null ? bill.getInvoiceReference() : null,
                // PointsTransaction context
                pt.getReason(),
                pt.getScanMethod() != null ? pt.getScanMethod().name() : null,
                pt.getMoneyAmount(),
                pt.getRuleAmountPer(),
                pt.getRulePointsPer(),
                (bill!=null && bill.getBusiness()!=null)? bill.getBusiness().getCurrencyCode(): null,
                // BillTransaction context
                bill != null ? bill.getInvoiceReference() : null,
                bill != null ? bill.getNote() : null
        );
    }

    private CustomerDetailsTabDto buildDetails(Business business) {
        BusinessCategory category = business.getBusinessType();
        String categoryLabel = category != null ? toTitleCase(category.name()) : "";

        return new CustomerDetailsTabDto(
                business.getBusinessDescription(),
                business.getBusinessPhoneNumber(),
                business.getBusinessEmail(),
                categoryLabel,
                business.getWebsiteUrl(),
                null,
                null
        );
    }

    private String deriveLoyaltyPolicy(LoyaltySettings loyaltySettings, EarningSettings earningSettings, String currency) {
        List<String> parts = new ArrayList<>();

        if (earningSettings != null && earningSettings.isEnabled() && earningSettings.isConfigured()) {
            String amountStr = earningSettings.getAmountPer().stripTrailingZeros().toPlainString();
            String currencyStr = currency != null ? " " + currency : "";
            parts.add("Earn " + earningSettings.getPointsPer() + " points for every " + amountStr + currencyStr + " spent.");
        }

        if (loyaltySettings != null && loyaltySettings.isEnabled() && loyaltySettings.isConfigured()) {
            parts.add("Redeem from " + loyaltySettings.getMinPointsToRedeem() + " points for a discount.");
            if (loyaltySettings.getExpiryType() == ExpiryType.EXPIRE_AFTER_X_MONTHS
                    && loyaltySettings.getMonthsToExpire() != null) {
                parts.add("Points expire after " + loyaltySettings.getMonthsToExpire() + " months of inactivity.");
            }
        }

        return parts.isEmpty() ? "Earn and redeem points at this business." : String.join(" ", parts);
    }

    private Integer calculateEarnedPoints(BigDecimal price, EarningSettings earningSettings) {
        if (earningSettings == null || !earningSettings.isEnabled() || !earningSettings.isConfigured()) return null;
        if (price == null) return null;
        return pointsCalculatorService.calculatePoints(price, earningSettings);
    }

    private String buildPointsLabel(EarningSettings earningSettings) {
        if (earningSettings == null || !earningSettings.isEnabled() || !earningSettings.isConfigured()) {
            return null;
        }
        return "+" + earningSettings.getPointsPer() + " pts";
    }

    private String buildMapLabel(Business business) {
        String city = business.getCity() != null ? business.getCity() : "";
        return business.getBusinessName() + (city.isBlank() ? "" : ", " + city);
    }

    private String deriveStatus(CustomerCoupon cc) {
        LocalDateTime now = LocalDateTime.now();
        if (cc.getStatus() == CustomerCouponStatus.REDEEMED) {
            if (cc.getExpiresAt() != null && cc.getExpiresAt().isBefore(now.plusDays(3))) {
                return "EXPIRING";
            }
            return "ACTIVE";
        }
        if (cc.getStatus() == CustomerCouponStatus.USED) {
            return "USED";
        }
        return "EXPIRED";
    }

    private String buildDiscountDisplay(LoyaltyCoupon coupon, BigDecimal pct, BigDecimal amt, CurrencyCode currency) {
        return switch (coupon.getType()) {
            case FREE_PRODUCT -> "Free Product";
            case PERCENTAGE_DISCOUNT -> pct != null ? pct.stripTrailingZeros().toPlainString() + "% Off" : null;
            case FIXED_AMOUNT_DISCOUNT -> amt != null
                    ? amt.stripTrailingZeros().toPlainString() + (currency != null ? " " + currency.getSymbol() : "") + " Off"
                    : null;
        };
    }

    private BigDecimal resolveDiscountValue(LoyaltyCoupon coupon, BigDecimal pct, BigDecimal amt) {
        return switch (coupon.getType()) {
            case PERCENTAGE_DISCOUNT -> pct;
            case FIXED_AMOUNT_DISCOUNT -> amt;
            case FREE_PRODUCT -> null;
        };
    }

    private String mapPromotionType(CouponType type) {
        return switch (type) {
            case PERCENTAGE_DISCOUNT -> "DISCOUNT_PERCENT";
            case FIXED_AMOUNT_DISCOUNT -> "DISCOUNT_FIXED";
            case FREE_PRODUCT -> "FREE_PRODUCT";
        };
    }

    private String mapTransactionType(PointsType type) {
        return switch (type) {
            case EARN_BILL -> "EARN";
            case REDEEM_DISCOUNT, REDEEM_OFFER -> "REDEEM";
            case COUPON_REDEMPTION -> "COUPON_PURCHASE";
            case EXPIRE -> "EXPIRED";
            case ADJUSTMENT_PLUS, ADJUSTMENT_MINUS, REVERSAL -> "ADJUSTMENT";
        };
    }

    private String toTitleCase(String name) {
        return Arrays.stream(name.split("_"))
                .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
