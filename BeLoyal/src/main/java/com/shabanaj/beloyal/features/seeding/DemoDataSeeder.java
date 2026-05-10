package com.shabanaj.beloyal.features.seeding;

import com.shabanaj.beloyal.features.billTransaction.repository.BillTransactionRepository;
import com.shabanaj.beloyal.features.business.repository.BusinessRepository;
import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.earningSettings.repository.EarningSettingsRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.loyaltyCard.repository.LoyaltyCardRepository;
import com.shabanaj.beloyal.features.loyaltySettings.repository.LoyaltySettingsRepository;
import com.shabanaj.beloyal.features.pointsBucket.repository.PointsBucketRepository;
import com.shabanaj.beloyal.features.pointsBucketConsumption.repository.PointsBucketConsumptionRepository;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.model.Entity.BillTransaction;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Entity.CatalogCategory;
import com.shabanaj.beloyal.model.Entity.CatalogItem;
import com.shabanaj.beloyal.model.Entity.CatalogItemVariant;
import com.shabanaj.beloyal.model.Entity.CouponDiscountDetails;
import com.shabanaj.beloyal.model.Entity.CouponFreeProductDetails;
import com.shabanaj.beloyal.model.Entity.CustomerCoupon;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.EarningSettings;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;
import com.shabanaj.beloyal.model.Entity.LoyaltyCoupon;
import com.shabanaj.beloyal.model.Entity.LoyaltySettings;
import com.shabanaj.beloyal.model.Entity.PointsBucket;
import com.shabanaj.beloyal.model.Entity.PointsBucketConsumption;
import com.shabanaj.beloyal.model.Entity.PointsTransaction;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.BusinessCategory;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import com.shabanaj.beloyal.model.Enums.CatalogItemType;
import com.shabanaj.beloyal.model.Enums.CatalogStatus;
import com.shabanaj.beloyal.model.Enums.CouponStatus;
import com.shabanaj.beloyal.model.Enums.CouponType;
import com.shabanaj.beloyal.model.Enums.CouponVisibility;
import com.shabanaj.beloyal.model.Enums.CurrencyCode;
import com.shabanaj.beloyal.model.Enums.CustomerCouponStatus;
import com.shabanaj.beloyal.model.Enums.ExpiryType;
import com.shabanaj.beloyal.model.Enums.Gender;
import com.shabanaj.beloyal.model.Enums.LoyaltyCardStatus;
import com.shabanaj.beloyal.model.Enums.PointsBucketStatus;
import com.shabanaj.beloyal.model.Enums.PointsType;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.model.Enums.ScanMethod;
import com.shabanaj.beloyal.model.Enums.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(value = "app.seed.demo.enabled", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
    private static final String MARKER_EMAIL = "seed.superadmin@beloyal.local";
    private static final String DEFAULT_PASSWORD = "SeededDemo123!";
    private static final DateTimeFormatter DATE_KEY = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.ROOT);

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final BusinessMemberRepository businessMemberRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final LoyaltyCardRepository loyaltyCardRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final LoyaltySettingsRepository loyaltySettingsRepository;
    private final EarningSettingsRepository earningSettingsRepository;
    private final CatalogCategoryRepository catalogCategoryRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final CatalogItemVariantRepository catalogItemVariantRepository;
    private final CouponRepository couponRepository;
    private final CustomerCouponRepository customerCouponRepository;
    private final PointsTransactionRepository pointsTransactionRepository;
    private final PointsBucketRepository pointsBucketRepository;
    private final PointsBucketConsumptionRepository pointsBucketConsumptionRepository;
    private final BillTransactionRepository billTransactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final int businessCount;
    private final int customerCount;

    public DemoDataSeeder(
            UserRepository userRepository,
            BusinessRepository businessRepository,
            BusinessMemberRepository businessMemberRepository,
            CustomerProfileRepository customerProfileRepository,
            LoyaltyCardRepository loyaltyCardRepository,
            LoyaltyAccountRepository loyaltyAccountRepository,
            LoyaltySettingsRepository loyaltySettingsRepository,
            EarningSettingsRepository earningSettingsRepository,
            CatalogCategoryRepository catalogCategoryRepository,
            CatalogItemRepository catalogItemRepository,
            CatalogItemVariantRepository catalogItemVariantRepository,
            CouponRepository couponRepository,
            CustomerCouponRepository customerCouponRepository,
            PointsTransactionRepository pointsTransactionRepository,
            PointsBucketRepository pointsBucketRepository,
            PointsBucketConsumptionRepository pointsBucketConsumptionRepository,
            BillTransactionRepository billTransactionRepository,
            PasswordEncoder passwordEncoder,
            Clock clock,
            @Value("${app.seed.demo.business-count:6}") int businessCount,
            @Value("${app.seed.demo.customer-count:64}") int customerCount
    ) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.businessMemberRepository = businessMemberRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.loyaltyCardRepository = loyaltyCardRepository;
        this.loyaltyAccountRepository = loyaltyAccountRepository;
        this.loyaltySettingsRepository = loyaltySettingsRepository;
        this.earningSettingsRepository = earningSettingsRepository;
        this.catalogCategoryRepository = catalogCategoryRepository;
        this.catalogItemRepository = catalogItemRepository;
        this.catalogItemVariantRepository = catalogItemVariantRepository;
        this.couponRepository = couponRepository;
        this.customerCouponRepository = customerCouponRepository;
        this.pointsTransactionRepository = pointsTransactionRepository;
        this.pointsBucketRepository = pointsBucketRepository;
        this.pointsBucketConsumptionRepository = pointsBucketConsumptionRepository;
        this.billTransactionRepository = billTransactionRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.businessCount = Math.max(1, businessCount);
        this.customerCount = Math.max(12, customerCount);
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.findUserByEmailIgnoreCase(MARKER_EMAIL).isPresent()) {
            log.info("Demo seed skipped because marker user {} already exists.", MARKER_EMAIL);
            return;
        }

        seedDemoData();
    }

    private void seedDemoData() {
        LocalDateTime now = LocalDateTime.now(clock);
        Random random = new Random(20260509L);

        User superAdmin = createPlatformAdmin(now);
        List<BusinessSeedContext> businesses = createBusinesses(superAdmin, now, random);
        List<CustomerProfile> customers = createCustomers(now, random);
        populateLoyaltyData(customers, businesses, now, random);

        log.info(
                "Demo seed finished: {} businesses, {} customers, {} loyalty accounts, {} coupons, {} points transactions.",
                businesses.size(),
                customers.size(),
                loyaltyAccountRepository.count(),
                couponRepository.count(),
                pointsTransactionRepository.count()
        );
        log.info("Seeded login password for generated users: {}", DEFAULT_PASSWORD);
    }

    private User createPlatformAdmin(LocalDateTime now) {
        User admin = baseUser(
                "Platform",
                "Admin",
                "seed.superadmin",
                MARKER_EMAIL,
                "+355690000001",
                now.minusMonths(18)
        );
        admin.setRoles(new HashSet<>(Set.of(Role.SUPER_ADMIN, Role.ADMIN)));
        return userRepository.save(admin);
    }

    private List<BusinessSeedContext> createBusinesses(User superAdmin, LocalDateTime now, Random random) {
        List<BusinessTemplate> templates = businessTemplates();
        List<BusinessSeedContext> contexts = new ArrayList<>();

        for (int i = 0; i < businessCount; i++) {
            BusinessTemplate template = templates.get(i % templates.size());
            String suffix = String.format(Locale.ROOT, "%02d", i + 1);

            Business business = new Business();
            business.setBusinessName(template.name() + " " + suffix);
            business.setBusinessType(template.category());
            business.setBusinessDescription(template.description());
            business.setAddress(template.addressPrefix() + " " + (20 + i));
            business.setCity(template.city());
            business.setCountry("Albania");
            business.setWebsiteUrl("https://demo-" + slug(template.name()) + suffix + ".beloyal.local");
            business.setVatId("DEMO-VAT-" + suffix);
            business.setBusinessPhoneNumber("+3556920" + String.format(Locale.ROOT, "%04d", 1000 + i));
            business.setBusinessEmail("business" + suffix + "@beloyal.local");
            business.setCurrencyCode(template.currency());
            business.setRating(3.8 + ((i % 10) * 0.12));
            business.setSubmittedAt(now.minusMonths(6).plusDays(i * 5L));
            business.setLogoPath("/uploads/demo/business-" + suffix + ".png");
            business.setLogoKey("demo/business/" + suffix + ".png");

            if (i == businessCount - 1 && businessCount > 2) {
                business.setBusinessStatus(BusinessStatus.INACTIVE);
                business.setReviewedAt(now.minusMonths(1));
                business.setReviewedByAdminId(superAdmin.getId());
            } else if (i == businessCount - 2 && businessCount > 3) {
                business.setBusinessStatus(BusinessStatus.PENDING_APPROVAL);
            } else {
                business.setBusinessStatus(BusinessStatus.ACTIVE);
                business.setReviewedAt(now.minusMonths(5).plusDays(i * 4L));
                business.setReviewedByAdminId(superAdmin.getId());
            }

            business = businessRepository.save(business);

            User adminUser = baseUser(
                    template.ownerFirstName(),
                    template.ownerLastName(),
                    "bizadmin" + suffix,
                    "bizadmin" + suffix + "@beloyal.local",
                    "+3556901" + String.format(Locale.ROOT, "%04d", 1000 + i),
                    now.minusMonths(15).plusDays(i)
            );
            adminUser = userRepository.save(adminUser);

            BusinessMember adminMember = new BusinessMember();
            adminMember.setBusiness(business);
            adminMember.setUser(adminUser);
            adminMember.setRole(Role.BUSINESS_ADMIN);
            adminMember.setMemberStatus(BusinessMember.MemberStatus.ACTIVE);
            adminMember.setHiredAt(LocalDate.now(clock).minusMonths(12).plusDays(i));
            adminMember = businessMemberRepository.save(adminMember);

            List<BusinessMember> members = new ArrayList<>();
            members.add(adminMember);

            for (int staffIndex = 0; staffIndex < 3; staffIndex++) {
                int n = (i * 3) + staffIndex + 1;
                User staffUser = baseUser(
                        pick(STAFF_FIRST_NAMES, n),
                        pick(STAFF_LAST_NAMES, n),
                        "staff" + suffix + staffIndex,
                        "staff" + suffix + staffIndex + "@beloyal.local",
                        "+3556902" + String.format(Locale.ROOT, "%04d", 1000 + n),
                        now.minusMonths(10).plusDays(n)
                );
                if (staffIndex == 2 && i % 2 == 1) {
                    staffUser.setStatus(UserStatus.DISABLED);
                }
                staffUser = userRepository.save(staffUser);

                BusinessMember staffMember = new BusinessMember();
                staffMember.setBusiness(business);
                staffMember.setUser(staffUser);
                staffMember.setRole(Role.STAFF);
                staffMember.setMemberStatus(staffIndex == 2 && i % 2 == 1
                        ? BusinessMember.MemberStatus.INACTIVE
                        : BusinessMember.MemberStatus.ACTIVE);
                staffMember.setHiredAt(LocalDate.now(clock).minusMonths(8).plusDays(n));
                members.add(businessMemberRepository.save(staffMember));
            }

            EarningSettings earningSettings = new EarningSettings();
            earningSettings.setBusiness(business);
            earningSettings.setEnabled(business.getBusinessStatus() == BusinessStatus.ACTIVE);
            earningSettings.setConfigured(true);
            earningSettings.setPointsPer(5 + (i % 4) * 5);
            earningSettings.setAmountPer(BigDecimal.valueOf(5 + (i % 3) * 2L).setScale(2, RoundingMode.HALF_UP));
            earningSettingsRepository.save(earningSettings);

            LoyaltySettings loyaltySettings = new LoyaltySettings();
            loyaltySettings.setBusiness(business);
            loyaltySettings.setConfigured(true);
            loyaltySettings.setEnabled(business.getBusinessStatus() == BusinessStatus.ACTIVE);
            loyaltySettings.setMinPointsToRedeem(40 + (i % 4) * 10);
            loyaltySettings.setMaxPointsToRedeem(600 + (i * 30));
            loyaltySettings.setPointsPerUnitDiscount(10);
            loyaltySettings.setMaxPointsPerTransactions(250 + (i * 10));
            loyaltySettings.setExpiryType(i % 2 == 0 ? ExpiryType.EXPIRE_AFTER_X_MONTHS : ExpiryType.NO_EXPIRY);
            loyaltySettings.setMonthsToExpire(i % 2 == 0 ? 12 : null);
            loyaltySettingsRepository.save(loyaltySettings);

            List<CatalogCategory> categories = createCategories(business, template.categoryNames());
            Map<Long, List<CatalogItem>> itemsByCategory = createCatalogItems(business, categories, template, random);
            List<CatalogItemVariant> variants = createVariants(itemsByCategory, random);
            List<LoyaltyCoupon> coupons = createCoupons(business, categories, itemsByCategory, variants, now, random);

            contexts.add(new BusinessSeedContext(
                    business,
                    earningSettings,
                    loyaltySettings,
                    members,
                    categories,
                    itemsByCategory,
                    variants,
                    coupons.stream()
                            .filter(coupon -> coupon.getStatus() == CouponStatus.ACTIVE && coupon.getDeletedAt() == null)
                            .sorted(Comparator.comparingInt(LoyaltyCoupon::getPointsCost))
                            .collect(Collectors.toCollection(ArrayList::new))
            ));
        }

        return contexts;
    }

    private List<CatalogCategory> createCategories(Business business, List<String> names) {
        List<CatalogCategory> categories = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            CatalogCategory category = CatalogCategory.builder()
                    .business(business)
                    .name(names.get(i))
                    .description(names.get(i) + " selection for " + business.getBusinessName())
                    .orderIndex(i)
                    .status(i == names.size() - 1 ? CatalogStatus.INACTIVE : CatalogStatus.ACTIVE)
                    .isDeleted(false)
                    .build();
            categories.add(catalogCategoryRepository.save(category));
        }
        return categories;
    }

    private Map<Long, List<CatalogItem>> createCatalogItems(
            Business business,
            List<CatalogCategory> categories,
            BusinessTemplate template,
            Random random
    ) {
        Map<Long, List<CatalogItem>> itemsByCategory = new HashMap<>();

        for (int categoryIndex = 0; categoryIndex < categories.size(); categoryIndex++) {
            CatalogCategory category = categories.get(categoryIndex);
            List<CatalogItem> categoryItems = new ArrayList<>();
            List<String> names = buildItemNames(template, category.getName());

            for (int itemIndex = 0; itemIndex < names.size(); itemIndex++) {
                BigDecimal price = BigDecimal.valueOf(3 + categoryIndex * 2L + itemIndex * 1.75 + random.nextDouble() * 4)
                        .setScale(2, RoundingMode.HALF_UP);

                CatalogItem item = CatalogItem.builder()
                        .business(business)
                        .category(category)
                        .name(names.get(itemIndex))
                        .description(names.get(itemIndex) + " prepared for fast demo browsing and frontend testing.")
                        .price(price)
                        .type(itemIndex == names.size() - 1 && categoryIndex == categories.size() - 1
                                ? CatalogItemType.SERVICE
                                : CatalogItemType.PRODUCT)
                        .currencyCode(business.getCurrencyCode())
                        .unit(itemIndex % 3 == 0 ? "plate" : itemIndex % 3 == 1 ? "cup" : "piece")
                        .status(itemIndex == names.size() - 1 && categoryIndex % 2 == 1
                                ? CatalogStatus.INACTIVE
                                : CatalogStatus.ACTIVE)
                        .imageUrl("/uploads/demo/catalog/" + business.getId() + "/" + slug(names.get(itemIndex)) + ".png")
                        .imageKey("demo/catalog/" + business.getId() + "/" + slug(names.get(itemIndex)) + ".png")
                        .orderIndex(itemIndex)
                        .isDeleted(false)
                        .build();
                categoryItems.add(catalogItemRepository.save(item));
            }

            itemsByCategory.put(category.getId(), categoryItems);
        }

        return itemsByCategory;
    }

    private List<CatalogItemVariant> createVariants(Map<Long, List<CatalogItem>> itemsByCategory, Random random) {
        List<CatalogItemVariant> variants = new ArrayList<>();

        for (List<CatalogItem> items : itemsByCategory.values()) {
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                CatalogItem item = items.get(itemIndex);
                if (itemIndex % 2 != 0) {
                    continue;
                }

                List<String> variantNames = List.of("Regular", "Large", "Combo");
                for (int variantIndex = 0; variantIndex < variantNames.size(); variantIndex++) {
                    CatalogItemVariant variant = CatalogItemVariant.builder()
                            .catalogItem(item)
                            .name(variantNames.get(variantIndex))
                            .description(variantNames.get(variantIndex) + " option for " + item.getName())
                            .priceOverride(item.getPrice().add(BigDecimal.valueOf(variantIndex * 1.50 + random.nextDouble())))
                            .status(variantIndex == 2 && itemIndex % 4 == 0 ? CatalogStatus.INACTIVE : CatalogStatus.ACTIVE)
                            .orderIndex(variantIndex)
                            .isDeleted(false)
                            .build();
                    variants.add(catalogItemVariantRepository.save(variant));
                }
            }
        }

        return variants;
    }

    private List<LoyaltyCoupon> createCoupons(
            Business business,
            List<CatalogCategory> categories,
            Map<Long, List<CatalogItem>> itemsByCategory,
            List<CatalogItemVariant> variants,
            LocalDateTime now,
            Random random
    ) {
        List<LoyaltyCoupon> coupons = new ArrayList<>();
        CatalogCategory featuredCategory = categories.getFirst();
        CatalogItem featuredItem = itemsByCategory.get(featuredCategory.getId()).getFirst();
        CatalogItemVariant featuredVariant = variants.stream()
                .filter(variant -> Objects.equals(variant.getCatalogItem().getId(), featuredItem.getId()))
                .findFirst()
                .orElse(null);

        coupons.add(saveDiscountCoupon(
                business,
                "15% Off Favorites",
                CouponType.PERCENTAGE_DISCOUNT,
                120,
                CouponStatus.ACTIVE,
                CouponVisibility.PUBLIC,
                now.minusDays(10),
                now.plusMonths(3),
                250,
                2,
                true,
                1,
                BigDecimal.valueOf(15),
                null,
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(15),
                null
        ));

        coupons.add(saveDiscountCoupon(
                business,
                "5 Euro Welcome Discount",
                CouponType.FIXED_AMOUNT_DISCOUNT,
                90,
                CouponStatus.ACTIVE,
                CouponVisibility.PUBLIC,
                now.minusDays(20),
                now.plusMonths(2),
                400,
                1,
                false,
                2,
                null,
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(18),
                BigDecimal.valueOf(5),
                null
        ));

        coupons.add(saveFreeProductCoupon(
                business,
                "Free " + featuredItem.getName(),
                150,
                CouponStatus.ACTIVE,
                now.minusDays(5),
                now.plusMonths(4),
                120,
                1,
                true,
                3,
                featuredCategory,
                featuredItem,
                featuredVariant,
                1
        ));

        coupons.add(saveDiscountCoupon(
                business,
                "VIP Night Pause",
                CouponType.PERCENTAGE_DISCOUNT,
                180,
                CouponStatus.PAUSED,
                CouponVisibility.HIDDEN,
                now.minusDays(30),
                now.plusMonths(1),
                50,
                1,
                false,
                4,
                BigDecimal.valueOf(25),
                null,
                BigDecimal.valueOf(40),
                BigDecimal.valueOf(30),
                null
        ));

        coupons.add(saveDiscountCoupon(
                business,
                "Draft Bundle Saver",
                CouponType.FIXED_AMOUNT_DISCOUNT,
                110,
                CouponStatus.DRAFT,
                CouponVisibility.PUBLIC,
                now.plusDays(2),
                now.plusMonths(1),
                null,
                1,
                false,
                5,
                null,
                BigDecimal.valueOf(7),
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(7),
                null
        ));

        coupons.add(saveFreeProductCoupon(
                business,
                "Expired Treat",
                80,
                CouponStatus.EXPIRED,
                now.minusMonths(4),
                now.minusMonths(1),
                80,
                1,
                false,
                6,
                featuredCategory,
                featuredItem,
                featuredVariant,
                1
        ));

        LoyaltyCoupon archived = saveDiscountCoupon(
                business,
                "Archived Weekend Discount",
                CouponType.PERCENTAGE_DISCOUNT,
                140,
                CouponStatus.ARCHIVED,
                CouponVisibility.PUBLIC,
                now.minusMonths(5),
                now.plusMonths(1),
                90,
                1,
                false,
                7,
                BigDecimal.valueOf(10),
                null,
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(8),
                now.minusDays(15)
        );
        coupons.add(archived);

        LoyaltyCoupon trashed = saveDiscountCoupon(
                business,
                "Trash Bin Offer",
                CouponType.FIXED_AMOUNT_DISCOUNT,
                70,
                CouponStatus.ARCHIVED,
                CouponVisibility.HIDDEN,
                now.minusMonths(2),
                now.plusDays(10),
                30,
                1,
                false,
                8,
                null,
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(3),
                now.minusDays(7)
        );
        trashed.setDeletedAt(now.minusDays(2));
        coupons.add(couponRepository.save(trashed));

        for (LoyaltyCoupon coupon : coupons) {
            coupon.setTotalRedemptions(random.nextInt(Math.max(1, coupon.getTotalRedemptionLimit() == null ? 12 : coupon.getTotalRedemptionLimit() / 4 + 1)));
            couponRepository.save(coupon);
        }

        return coupons;
    }

    private LoyaltyCoupon saveDiscountCoupon(
            Business business,
            String title,
            CouponType type,
            int pointsCost,
            CouponStatus status,
            CouponVisibility visibility,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer totalLimit,
            Integer perCustomerLimit,
            boolean featured,
            Integer sortOrder,
            BigDecimal discountPercentage,
            BigDecimal discountAmount,
            BigDecimal minimumOrderAmount,
            BigDecimal maximumDiscountAmount,
            LocalDateTime archivedAt
    ) {
        LoyaltyCoupon coupon = LoyaltyCoupon.builder()
                .business(business)
                .type(type)
                .title(title)
                .description(title + " generated as demo data for frontend coverage.")
                .imageUrl("/uploads/demo/coupons/" + business.getId() + "/" + slug(title) + ".png")
                .pointsCost(pointsCost)
                .currency(business.getCurrencyCode())
                .status(status)
                .visibility(visibility)
                .startDate(startDate)
                .endDate(endDate)
                .totalRedemptionLimit(totalLimit)
                .perCustomerRedemptionLimit(perCustomerLimit)
                .termsAndConditions("Demo coupon. Do not use in production.")
                .isFeatured(featured)
                .sortOrder(sortOrder)
                .archivedAt(archivedAt)
                .build();

        CouponDiscountDetails details = CouponDiscountDetails.builder()
                .coupon(coupon)
                .discountPercentage(discountPercentage)
                .discountAmount(discountAmount)
                .minimumOrderAmount(minimumOrderAmount)
                .maximumDiscountAmount(maximumDiscountAmount)
                .build();
        coupon.setDiscountDetails(details);
        return couponRepository.save(coupon);
    }

    private LoyaltyCoupon saveFreeProductCoupon(
            Business business,
            String title,
            int pointsCost,
            CouponStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer totalLimit,
            Integer perCustomerLimit,
            boolean featured,
            Integer sortOrder,
            CatalogCategory category,
            CatalogItem product,
            CatalogItemVariant variant,
            int quantity
    ) {
        LoyaltyCoupon coupon = LoyaltyCoupon.builder()
                .business(business)
                .type(CouponType.FREE_PRODUCT)
                .title(title)
                .description(title + " generated as demo data for frontend coverage.")
                .imageUrl("/uploads/demo/coupons/" + business.getId() + "/" + slug(title) + ".png")
                .pointsCost(pointsCost)
                .currency(business.getCurrencyCode())
                .status(status)
                .visibility(CouponVisibility.PUBLIC)
                .startDate(startDate)
                .endDate(endDate)
                .totalRedemptionLimit(totalLimit)
                .perCustomerRedemptionLimit(perCustomerLimit)
                .termsAndConditions("Demo coupon. Do not use in production.")
                .isFeatured(featured)
                .sortOrder(sortOrder)
                .build();

        CouponFreeProductDetails details = CouponFreeProductDetails.builder()
                .coupon(coupon)
                .category(category)
                .product(product)
                .variant(variant)
                .quantity(quantity)
                .build();
        coupon.setFreeProductDetails(details);
        return couponRepository.save(coupon);
    }

    private List<CustomerProfile> createCustomers(LocalDateTime now, Random random) {
        List<CustomerProfile> customers = new ArrayList<>();

        for (int i = 0; i < customerCount; i++) {
            String suffix = String.format(Locale.ROOT, "%03d", i + 1);
            String firstName = pick(CUSTOMER_FIRST_NAMES, i);
            String lastName = pick(CUSTOMER_LAST_NAMES, i + 7);
            User user = baseUser(
                    firstName,
                    lastName,
                    "customer" + suffix,
                    "customer" + suffix + "@beloyal.local",
                    "+355691" + String.format(Locale.ROOT, "%05d", 10000 + i),
                    now.minusMonths(12).plusDays(i)
            );
            user.setRoles(new HashSet<>(Set.of(Role.CUSTOMER)));
            user = userRepository.save(user);

            CustomerProfile profile = new CustomerProfile();
            profile.setUser(user);
            profile.setReferralCode("REF" + suffix);
            profile.setReferredBy(i == 0 ? null : "REF" + String.format(Locale.ROOT, "%03d", Math.max(1, i)));
            profile.setBirthDate(LocalDate.of(1988 + (i % 12), Month.JANUARY.plus(i % 12), 3 + (i % 24)));
            profile.setGender(Gender.values()[i % Gender.values().length]);
            profile.setCity(i % 3 == 0 ? "Tirane" : i % 3 == 1 ? "Durres" : "Vlore");
            profile.setCountry("Albania");
            profile.setNotificationEnabled(i % 6 != 0);
            profile = customerProfileRepository.save(profile);

            LoyaltyCard card = new LoyaltyCard();
            card.setCustomerProfile(profile);
            card.setQrToken("QR-DEMO-" + suffix + "-" + DATE_KEY.format(now.minusDays(i)));
            card.setManualCode("LC" + String.format(Locale.ROOT, "%06d", 100000 + i));
            card.setStatus(i % 17 == 0 ? LoyaltyCardStatus.SUSPENDED : LoyaltyCardStatus.ACTIVE);
            card.setIssuedAt(now.minusMonths(10).plusDays(i));
            card.setLastUsedAt(now.minusDays(i % 14));
            loyaltyCardRepository.save(card);

            customers.add(profile);
        }

        return customers;
    }

    private void populateLoyaltyData(
            List<CustomerProfile> customers,
            List<BusinessSeedContext> businesses,
            LocalDateTime now,
            Random random
    ) {
        List<BusinessSeedContext> activeBusinesses = businesses.stream()
                .filter(context -> context.business().getBusinessStatus() == BusinessStatus.ACTIVE)
                .toList();

        for (int customerIndex = 0; customerIndex < customers.size(); customerIndex++) {
            CustomerProfile profile = customers.get(customerIndex);
            int memberships = 2 + (customerIndex % Math.min(3, Math.max(1, activeBusinesses.size() - 1)));
            List<BusinessSeedContext> selectedBusinesses = rotate(activeBusinesses, customerIndex, memberships);

            for (int membershipIndex = 0; membershipIndex < selectedBusinesses.size(); membershipIndex++) {
                BusinessSeedContext context = selectedBusinesses.get(membershipIndex);
                LoyaltyAccount account = new LoyaltyAccount();
                account.setCustomerProfile(profile);
                account.setBusiness(context.business());
                account.setAvailablePoints(0);
                account.setLifetimeEarned(0);
                account.setLifetimeRedeemed(0);
                account.setLifetimeExpired(0);
                account = loyaltyAccountRepository.save(account);

                AccountRuntime runtime = new AccountRuntime(account);
                createEarnHistory(runtime, context, now, random, customerIndex, membershipIndex);
                createRedemptionHistory(runtime, context, now, random, customerIndex, membershipIndex);
                finalizeAccount(runtime, now, random);
            }
        }
    }

    private void createEarnHistory(
            AccountRuntime runtime,
            BusinessSeedContext context,
            LocalDateTime now,
            Random random,
            int customerIndex,
            int membershipIndex
    ) {
        int earnTransactions = 3 + ((customerIndex + membershipIndex) % 4);

        for (int i = 0; i < earnTransactions; i++) {
            LocalDateTime transactionTime = now.minusDays(150L - (customerIndex % 20) * 3L - i * 11L - membershipIndex * 5L);
            BusinessMember performer = pickActiveMember(context.members(), customerIndex + i);
            BigDecimal grossAmount = BigDecimal.valueOf(18 + (customerIndex % 7) * 4L + i * 6L + random.nextDouble() * 5)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal discountAmount = BigDecimal.valueOf(i % 3 == 0 ? 2 : 0).setScale(2, RoundingMode.HALF_UP);
            BigDecimal netAmount = grossAmount.subtract(discountAmount).max(BigDecimal.ONE).setScale(2, RoundingMode.HALF_UP);
            int points = calculateEarnedPoints(netAmount, context.earningSettings());

            BillTransaction billTransaction = BillTransaction.builder()
                    .business(context.business())
                    .businessMember(performer)
                    .billAmount(grossAmount)
                    .discountAmount(discountAmount)
                    .netAmount(netAmount)
                    .invoiceReference("INV-" + context.business().getId() + "-" + runtime.account().getId() + "-" + i)
                    .note("Seeded earn bill")
                    .idempotencyKey("earn-" + context.business().getId() + "-" + runtime.account().getId() + "-" + i)
                    .requestHash(sha256("earn|" + context.business().getId() + '|' + runtime.account().getId() + '|' + i))
                    .build();
            billTransaction = billTransactionRepository.save(billTransaction);

            PointsTransaction earnTransaction = PointsTransaction.builder()
                    .loyaltyAccount(runtime.account())
                    .billTransaction(billTransaction)
                    .businessMember(performer)
                    .type(PointsType.EARN_BILL)
                    .description("Earned from bill " + billTransaction.getInvoiceReference())
                    .reason("Seeded loyalty earning")
                    .scanMethod(i % 2 == 0 ? ScanMethod.QR_CODE : ScanMethod.MANUAL_CODE)
                    .moneyAmount(netAmount)
                    .discountAmount(discountAmount)
                    .pointsDelta(points)
                    .ruleAmountPer(context.earningSettings().getAmountPer())
                    .rulePointsPer(context.earningSettings().getPointsPer())
                    .build();
            earnTransaction = pointsTransactionRepository.save(earnTransaction);

            PointsBucket bucket = PointsBucket.builder()
                    .loyaltyAccount(runtime.account())
                    .sourceTransaction(earnTransaction)
                    .pointsEarned(points)
                    .pointsRemaining(points)
                    .status(PointsBucketStatus.ACTIVE)
                    .earnedAt(transactionTime)
                    .expiresAt(resolveExpiry(context.loyaltySettings(), transactionTime))
                    .build();
            bucket = pointsBucketRepository.save(bucket);

            runtime.buckets().add(bucket);
            runtime.account().setAvailablePoints(runtime.account().getAvailablePoints() + points);
            runtime.account().setLifetimeEarned(runtime.account().getLifetimeEarned() + points);
            runtime.account().setLastActivityAt(transactionTime);
        }
    }

    private void createRedemptionHistory(
            AccountRuntime runtime,
            BusinessSeedContext context,
            LocalDateTime now,
            Random random,
            int customerIndex,
            int membershipIndex
    ) {
        if (runtime.account().getAvailablePoints() <= 0) {
            return;
        }

        boolean shouldRedeemCoupon = (customerIndex + membershipIndex) % 2 == 0;
        if (shouldRedeemCoupon) {
            LoyaltyCoupon coupon = context.activeCoupons().stream()
                    .filter(candidate -> candidate.getPointsCost() <= runtime.account().getAvailablePoints())
                    .findFirst()
                    .orElse(null);
            if (coupon != null) {
                LocalDateTime redeemedAt = now.minusDays(10L + ((customerIndex + membershipIndex) % 21));
                PointsTransaction couponTransaction = PointsTransaction.builder()
                        .loyaltyAccount(runtime.account())
                        .businessMember(pickActiveMember(context.members(), customerIndex + membershipIndex + 4))
                        .type(PointsType.COUPON_REDEMPTION)
                        .description("Redeemed coupon " + coupon.getTitle())
                        .reason("Seeded coupon redemption")
                        .scanMethod(customerIndex % 2 == 0 ? ScanMethod.QR_CODE : ScanMethod.MANUAL_CODE)
                        .moneyAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                        .discountAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                        .pointsDelta(-coupon.getPointsCost())
                        .build();
                couponTransaction = pointsTransactionRepository.save(couponTransaction);
                consumeBuckets(runtime, couponTransaction, coupon.getPointsCost());

                CustomerCouponStatus couponStatus = switch ((customerIndex + membershipIndex) % 4) {
                    case 0 -> CustomerCouponStatus.REDEEMED;
                    case 1 -> CustomerCouponStatus.USED;
                    case 2 -> CustomerCouponStatus.EXPIRED;
                    default -> CustomerCouponStatus.CANCELLED;
                };

                CustomerCoupon customerCoupon = CustomerCoupon.builder()
                        .coupon(coupon)
                        .business(context.business())
                        .customerProfile(runtime.account().getCustomerProfile())
                        .status(couponStatus)
                        .redeemedAt(redeemedAt)
                        .usedAt(couponStatus == CustomerCouponStatus.USED ? redeemedAt.plusDays(2) : null)
                        .expiresAt(coupon.getEndDate())
                        .orderId("CC-" + runtime.account().getId() + "-" + coupon.getId())
                        .pointsSpent(coupon.getPointsCost())
                        .currency(coupon.getCurrency())
                        .snapshotTitle(coupon.getTitle())
                        .snapshotDescription(coupon.getDescription())
                        .snapshotImageUrl(coupon.getImageUrl())
                        .snapshotCouponType(coupon.getType())
                        .snapshotProductId(coupon.getFreeProductDetails() != null ? coupon.getFreeProductDetails().getProduct().getId() : null)
                        .snapshotVariantId(coupon.getFreeProductDetails() != null && coupon.getFreeProductDetails().getVariant() != null
                                ? coupon.getFreeProductDetails().getVariant().getId()
                                : null)
                        .snapshotDiscountPercentage(coupon.getDiscountDetails() != null ? coupon.getDiscountDetails().getDiscountPercentage() : null)
                        .snapshotDiscountAmount(coupon.getDiscountDetails() != null ? coupon.getDiscountDetails().getDiscountAmount() : null)
                        .snapshotMinimumOrderAmount(coupon.getDiscountDetails() != null ? coupon.getDiscountDetails().getMinimumOrderAmount() : null)
                        .snapshotMaximumDiscountAmount(coupon.getDiscountDetails() != null ? coupon.getDiscountDetails().getMaximumDiscountAmount() : null)
                        .build();
                customerCouponRepository.save(customerCoupon);

                coupon.setTotalRedemptions(coupon.getTotalRedemptions() + 1);
                couponRepository.save(coupon);
            }
        }

        boolean shouldRedeemDiscount = runtime.account().getAvailablePoints() >= context.loyaltySettings().getMinPointsToRedeem()
                && (customerIndex + membershipIndex) % 3 == 0;
        if (shouldRedeemDiscount) {
            int requestedPoints = Math.min(
                    runtime.account().getAvailablePoints(),
                    Math.min(context.loyaltySettings().getMaxPointsToRedeem(), context.loyaltySettings().getMinPointsToRedeem() + 20)
            );
            if (requestedPoints > 0) {
                BigDecimal discountAmount = BigDecimal.valueOf(requestedPoints)
                        .divide(BigDecimal.valueOf(context.loyaltySettings().getPointsPerUnitDiscount()), 2, RoundingMode.HALF_UP);
                BigDecimal grossAmount = BigDecimal.valueOf(25 + (customerIndex % 8) * 4L).setScale(2, RoundingMode.HALF_UP);
                BigDecimal netAmount = grossAmount.subtract(discountAmount).max(BigDecimal.ONE).setScale(2, RoundingMode.HALF_UP);

                BillTransaction billTransaction = BillTransaction.builder()
                        .business(context.business())
                        .businessMember(pickActiveMember(context.members(), customerIndex + membershipIndex + 9))
                        .billAmount(grossAmount)
                        .discountAmount(discountAmount)
                        .netAmount(netAmount)
                        .invoiceReference("RDM-" + context.business().getId() + "-" + runtime.account().getId())
                        .note("Seeded points redemption")
                        .idempotencyKey("redeem-" + context.business().getId() + "-" + runtime.account().getId())
                        .requestHash(sha256("redeem|" + context.business().getId() + '|' + runtime.account().getId()))
                        .build();
                billTransaction = billTransactionRepository.save(billTransaction);

                PointsTransaction redeemTransaction = PointsTransaction.builder()
                        .loyaltyAccount(runtime.account())
                        .billTransaction(billTransaction)
                        .businessMember(billTransaction.getBusinessMember())
                        .type(PointsType.REDEEM_DISCOUNT)
                        .description("Redeemed points for bill discount")
                        .reason("Seeded bill redemption")
                        .scanMethod(customerIndex % 2 == 0 ? ScanMethod.QR_CODE : ScanMethod.MANUAL_CODE)
                        .moneyAmount(netAmount)
                        .discountAmount(discountAmount)
                        .pointsDelta(-requestedPoints)
                        .ruleAmountPer(BigDecimal.ONE)
                        .rulePointsPer(context.loyaltySettings().getPointsPerUnitDiscount())
                        .build();
                redeemTransaction = pointsTransactionRepository.save(redeemTransaction);
                consumeBuckets(runtime, redeemTransaction, requestedPoints);
            }
        }
    }

    private void finalizeAccount(AccountRuntime runtime, LocalDateTime now, Random random) {
        runtime.buckets().sort(Comparator.comparing(PointsBucket::getEarnedAt));
        LocalDateTime lastActivity = runtime.account().getLastActivityAt();
        if (lastActivity == null) {
            lastActivity = now.minusDays(1);
        }
        runtime.account().setLastActivityAt(lastActivity.plusHours(random.nextInt(12)));
        loyaltyAccountRepository.save(runtime.account());
        pointsBucketRepository.saveAll(runtime.buckets());
    }

    private void consumeBuckets(AccountRuntime runtime, PointsTransaction transaction, int pointsToSpend) {
        int remaining = pointsToSpend;
        runtime.buckets().sort(Comparator.comparing(PointsBucket::getEarnedAt));

        for (PointsBucket bucket : runtime.buckets()) {
            if (remaining <= 0) {
                break;
            }
            if (bucket.getStatus() != PointsBucketStatus.ACTIVE || bucket.getPointsRemaining() <= 0) {
                continue;
            }

            int consumed = Math.min(bucket.getPointsRemaining(), remaining);
            bucket.setPointsRemaining(bucket.getPointsRemaining() - consumed);
            bucket.setStatus(bucket.getPointsRemaining() == 0 ? PointsBucketStatus.FULLY_USED : PointsBucketStatus.ACTIVE);

            PointsBucketConsumption consumption = new PointsBucketConsumption();
            consumption.setTransaction(transaction);
            consumption.setPointsBucket(bucket);
            consumption.setPointsUsed(consumed);
            pointsBucketConsumptionRepository.save(consumption);

            remaining -= consumed;
        }

        runtime.account().setAvailablePoints(runtime.account().getAvailablePoints() - (pointsToSpend - remaining));
        runtime.account().setLifetimeRedeemed(runtime.account().getLifetimeRedeemed() + (pointsToSpend - remaining));
        runtime.account().setLastActivityAt(LocalDateTime.now(clock));
    }

    private int calculateEarnedPoints(BigDecimal netAmount, EarningSettings earningSettings) {
        BigDecimal quotient = netAmount.divide(earningSettings.getAmountPer(), 0, RoundingMode.DOWN);
        int points = quotient.intValue() * earningSettings.getPointsPer();
        return Math.max(points, earningSettings.getPointsPer());
    }

    private LocalDateTime resolveExpiry(LoyaltySettings loyaltySettings, LocalDateTime earnedAt) {
        if (loyaltySettings.getExpiryType() == ExpiryType.EXPIRE_AFTER_X_MONTHS && loyaltySettings.getMonthsToExpire() != null) {
            return earnedAt.plusMonths(loyaltySettings.getMonthsToExpire());
        }
        return null;
    }

    private BusinessMember pickActiveMember(List<BusinessMember> members, int index) {
        List<BusinessMember> active = members.stream()
                .filter(member -> member.getMemberStatus() == BusinessMember.MemberStatus.ACTIVE)
                .toList();
        return active.get(index % active.size());
    }

    private List<BusinessSeedContext> rotate(List<BusinessSeedContext> source, int startIndex, int count) {
        List<BusinessSeedContext> result = new ArrayList<>();
        for (int i = 0; i < Math.min(count, source.size()); i++) {
            result.add(source.get((startIndex + i) % source.size()));
        }
        return result;
    }

    private User baseUser(
            String firstName,
            String lastName,
            String username,
            String email,
            String phone,
            LocalDateTime lastLoginAt
    ) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setPhoneNumber(phone);
        user.setProfileImage("/uploads/demo/users/" + username + ".png");
        user.setProfileImageKey("demo/users/" + username + ".png");
        user.setStatus(UserStatus.ENABLED);
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(lastLoginAt.minusDays(10));
        user.setLastLoginAt(lastLoginAt);
        user.setAcceptedTcVersion("2026.05-demo");
        user.setAcceptedTcAt(lastLoginAt.minusDays(12));
        return user;
    }

    private String slug(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format(Locale.ROOT, "%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private List<String> buildItemNames(BusinessTemplate template, String categoryName) {
        List<String> base = switch (template.category()) {
            case CAFE -> List.of("House Blend", "Cold Brew", "Flat White", "Seasonal Toast", "Signature Cake", "Coffee Workshop");
            case BAR -> List.of("Citrus Spritz", "Smoked Negroni", "Tap Lager", "Craft IPA", "Loaded Nachos", "Mixology Session");
            case FAST_FOOD -> List.of("Classic Burger", "Crispy Chicken", "Loaded Fries", "Wrap Supreme", "Milkshake", "Party Combo Setup");
            case PUB -> List.of("Steak Pie", "Fish and Chips", "Onion Rings", "Pub Burger", "Apple Crumble", "Trivia Night Booking");
            default -> List.of("Chef Special", "Garden Plate", "Roasted Bowl", "Signature Pasta", "Fresh Dessert", "Private Tasting");
        };

        return base.stream()
                .map(name -> categoryName + " " + name)
                .toList();
    }

    private List<BusinessTemplate> businessTemplates() {
        return List.of(
                new BusinessTemplate(
                        "Mora Bistro",
                        BusinessCategory.RESTAURANT,
                        CurrencyCode.EURO,
                        "Modern neighborhood restaurant with a loyalty-first demo menu.",
                        "Rruga e Kavajes",
                        "Tirane",
                        "Arben",
                        "Hoxha",
                        List.of("Starters", "Mains", "Desserts", "Drinks", "Experiences")
                ),
                new BusinessTemplate(
                        "Bean & Bloom",
                        BusinessCategory.CAFE,
                        CurrencyCode.EURO,
                        "Cafe concept focused on fast repeat visits and coupon redemption.",
                        "Bulevardi Deshmoret e Kombit",
                        "Tirane",
                        "Elira",
                        "Kola",
                        List.of("Coffee", "Tea", "Bakery", "Lunch", "Workshops")
                ),
                new BusinessTemplate(
                        "Copper Room",
                        BusinessCategory.BAR,
                        CurrencyCode.EURO,
                        "Cocktail bar demo business with high-variation catalog items.",
                        "Rruga Pjeter Bogdani",
                        "Tirane",
                        "Dren",
                        "Leka",
                        List.of("Cocktails", "Spirits", "Beer", "Snacks", "Events")
                ),
                new BusinessTemplate(
                        "Quick Bite",
                        BusinessCategory.FAST_FOOD,
                        CurrencyCode.EURO,
                        "Fast-food operation tuned for volume and points earning flows.",
                        "Rruga Taulantia",
                        "Durres",
                        "Megi",
                        "Meta",
                        List.of("Burgers", "Wraps", "Sides", "Drinks", "Bundles")
                ),
                new BusinessTemplate(
                        "North Yard Pub",
                        BusinessCategory.PUB,
                        CurrencyCode.EURO,
                        "Pub-style business with a mix of active and archived promotions.",
                        "Rruga Ismail Qemali",
                        "Vlore",
                        "Saimir",
                        "Prenga",
                        List.of("Grill", "Classics", "Sharing", "Desserts", "Bookings")
                ),
                new BusinessTemplate(
                        "Luna Table",
                        BusinessCategory.RESTAURANT,
                        CurrencyCode.EURO,
                        "Editorial-style restaurant demo with premium-looking data density.",
                        "Rruga Myslym Shyri",
                        "Tirane",
                        "Ina",
                        "Berisha",
                        List.of("Seasonal", "House Specials", "Brunch", "Beverages", "Chef Events")
                )
        );
    }

    private String pick(List<String> values, int index) {
        return values.get(index % values.size());
    }

    private record BusinessSeedContext(
            Business business,
            EarningSettings earningSettings,
            LoyaltySettings loyaltySettings,
            List<BusinessMember> members,
            List<CatalogCategory> categories,
            Map<Long, List<CatalogItem>> itemsByCategory,
            List<CatalogItemVariant> variants,
            List<LoyaltyCoupon> activeCoupons
    ) {
    }

    private record BusinessTemplate(
            String name,
            BusinessCategory category,
            CurrencyCode currency,
            String description,
            String addressPrefix,
            String city,
            String ownerFirstName,
            String ownerLastName,
            List<String> categoryNames
    ) {
    }

    private record AccountRuntime(LoyaltyAccount account, List<PointsBucket> buckets) {
        private AccountRuntime(LoyaltyAccount account) {
            this(account, new ArrayList<>());
        }
    }

    private static final List<String> STAFF_FIRST_NAMES = List.of(
            "Aida", "Blerim", "Diona", "Ermal", "Fiona", "Gent", "Hana", "Ilir", "Jora", "Klea", "Luan", "Mira"
    );

    private static final List<String> STAFF_LAST_NAMES = List.of(
            "Gjoka", "Nika", "Rama", "Dervishi", "Krasniqi", "Basha", "Doda", "Hasani", "Pepa", "Gega", "Lleshi", "Muca"
    );

    private static final List<String> CUSTOMER_FIRST_NAMES = List.of(
            "Aldi", "Besa", "Dion", "Era", "Flora", "Genti", "Helga", "Iris", "Joni", "Klea", "Leon", "Megi", "Nora", "Olsi", "Pegi", "Qendrim"
    );

    private static final List<String> CUSTOMER_LAST_NAMES = List.of(
            "Abazi", "Bajrami", "Cani", "Dema", "Elezi", "Ferati", "Gjoni", "Hysa", "Islami", "Jaku", "Koka", "Luma", "Matoshi", "Ndoja", "Pula", "Qosja"
    );
}
