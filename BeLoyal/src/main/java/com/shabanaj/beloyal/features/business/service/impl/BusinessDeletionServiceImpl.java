package com.shabanaj.beloyal.features.business.service.impl;

import com.shabanaj.beloyal.features.billTransaction.repository.BillTransactionRepository;
import com.shabanaj.beloyal.features.business.repository.BusinessRepository;
import com.shabanaj.beloyal.features.business.service.BusinessDeletionService;
import com.shabanaj.beloyal.features.businessMember.repository.BusinessMemberRepository;
import com.shabanaj.beloyal.features.catalogCategories.repository.CatalogCategoryRepository;
import com.shabanaj.beloyal.features.catalogItemVariants.repository.CatalogItemVariantRepository;
import com.shabanaj.beloyal.features.catalogItems.repository.CatalogItemRepository;
import com.shabanaj.beloyal.features.coupon.repository.CouponDiscountDetailsRepository;
import com.shabanaj.beloyal.features.coupon.repository.CouponFreeProductDetailsRepository;
import com.shabanaj.beloyal.features.coupon.repository.CouponRepository;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.earningSettings.repository.EarningSettingsRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.loyaltySettings.repository.LoyaltySettingsRepository;
import com.shabanaj.beloyal.features.pointsBucket.repository.PointsBucketRepository;
import com.shabanaj.beloyal.features.pointsBucketConsumption.repository.PointsBucketConsumptionRepository;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.token.repository.RefreshTokenRepository;
import com.shabanaj.beloyal.features.token.repository.ResetPasswordTokenRepository;
import com.shabanaj.beloyal.features.token.repository.StaffInviteTokenRepository;
import com.shabanaj.beloyal.features.token.repository.VerificationTokenRepository;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.model.Entity.BusinessMember;
import com.shabanaj.beloyal.model.Enums.Role;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessDeletionServiceImpl implements BusinessDeletionService {

    private final EntityManager entityManager;
    private final BusinessRepository businessRepository;
    private final BusinessMemberRepository businessMemberRepository;
    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final PointsBucketConsumptionRepository pointsBucketConsumptionRepository;
    private final PointsBucketRepository pointsBucketRepository;
    private final PointsTransactionRepository pointsTransactionRepository;
    private final BillTransactionRepository billTransactionRepository;
    private final CustomerCouponRepository customerCouponRepository;
    private final CouponDiscountDetailsRepository couponDiscountDetailsRepository;
    private final CouponFreeProductDetailsRepository couponFreeProductDetailsRepository;
    private final CouponRepository couponRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final LoyaltySettingsRepository loyaltySettingsRepository;
    private final EarningSettingsRepository earningSettingsRepository;
    private final CatalogItemVariantRepository catalogItemVariantRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final CatalogCategoryRepository catalogCategoryRepository;
    private final StaffInviteTokenRepository staffInviteTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    @Transactional
    public void deleteBusinessHard(Long businessId) {
        log.info("Starting business deletion: businessId={}", businessId);

        // Get member user IDs and emails before deleting anything
        List<BusinessMember> members = businessMemberRepository.findALlByBusinessId(businessId);
        Map<Long, String> memberUserEmails = new HashMap<>();
        for (BusinessMember member : members) {
            memberUserEmails.put(member.getUser().getId(), member.getUser().getEmail());
        }
        log.info("Found {} business members for deletion in business: businessId={}", members.size(), businessId);

        // Clear persistence context to prevent cascade issues when deleting related entities
        entityManager.clear();

        // Delete in reverse order of FK dependencies
        // 1. Points bucket consumption depends on points transaction and bucket
        pointsBucketConsumptionRepository.deleteByBusinessId(businessId);
        log.debug("Deleted points bucket consumptions for business: businessId={}", businessId);

        // 2. Points buckets before transactions (buckets hold FK to transactions)
        pointsBucketRepository.deleteByBusinessId(businessId);
        log.debug("Deleted points buckets for business: businessId={}", businessId);

        pointsTransactionRepository.deleteByBusinessId(businessId);
        log.debug("Deleted points transactions for business: businessId={}", businessId);

        // 3. Bill transactions
        billTransactionRepository.deleteByBusinessId(businessId);
        log.debug("Deleted bill transactions for business: businessId={}", businessId);

        // 4. Customer coupons and coupon details
        customerCouponRepository.deleteByBusinessId(businessId);
        log.debug("Deleted customer coupons for business: businessId={}", businessId);

        couponDiscountDetailsRepository.deleteByBusinessId(businessId);
        log.debug("Deleted coupon discount details for business: businessId={}", businessId);

        couponFreeProductDetailsRepository.deleteByBusinessId(businessId);
        log.debug("Deleted coupon free product details for business: businessId={}", businessId);

        couponRepository.deleteByBusinessId(businessId);
        log.debug("Deleted coupons for business: businessId={}", businessId);

        // 5. Loyalty accounts (depends on business)
        loyaltyAccountRepository.deleteByBusinessId(businessId);
        log.debug("Deleted loyalty accounts for business: businessId={}", businessId);

        // 6. Loyalty and earning settings
        loyaltySettingsRepository.deleteByBusinessId(businessId);
        log.debug("Deleted loyalty settings for business: businessId={}", businessId);

        earningSettingsRepository.deleteByBusinessId(businessId);
        log.debug("Deleted earning settings for business: businessId={}", businessId);

        // 7. Catalog items and variants
        catalogItemVariantRepository.deleteByBusinessId(businessId);
        log.debug("Deleted catalog item variants for business: businessId={}", businessId);

        catalogItemRepository.deleteByBusinessId(businessId);
        log.debug("Deleted catalog items for business: businessId={}", businessId);

        catalogCategoryRepository.deleteByBusinessId(businessId);
        log.debug("Deleted catalog categories for business: businessId={}", businessId);

        // 8. Staff invite tokens for this business
        staffInviteTokenRepository.deleteByBusinessId(businessId);
        log.debug("Deleted staff invite tokens for business: businessId={}", businessId);

        // 9. Collect user deletion decisions based on fresh queries
        Map<Long, String> usersToDelete = new HashMap<>();

        for (Long userId : memberUserEmails.keySet()) {
            String userEmail = memberUserEmails.get(userId);
            long otherMemberships = businessMemberRepository.countOtherMembershipsForUser(userId, businessId);

            if (otherMemberships == 0 && !userRepository.findById(userId).get().getRoles().contains(Role.CUSTOMER)) {
                usersToDelete.put(userId, userEmail);
                log.info("Marking user for deletion (no other memberships, not a CUSTOMER): userId={}, email={}", userId, userEmail);
            } else {
                log.info("Marking user to keep (has other memberships or is CUSTOMER): userId={}, email={}, otherMemberships={}", userId, userEmail, otherMemberships);
            }
        }

        // Delete all business members once
        businessMemberRepository.deleteByBusinessId(businessId);
        log.info("Deleted all {} business members for business: businessId={}", members.size(), businessId);

        // Delete collected users
        for (Map.Entry<Long, String> userEntry : usersToDelete.entrySet()) {
            Long userId = userEntry.getKey();
            String userEmail = userEntry.getValue();
            log.info("Deleting user: userId={}, email={}", userId, userEmail);
            deleteUser(userId);
        }

        // 10. Delete the business itself
        businessRepository.deleteById(businessId);
        log.info("Successfully deleted business: businessId={}", businessId);
    }

    private void deleteUser(Long userId) {
        log.debug("Starting user deletion: userId={}", userId);

        // Delete customer profile if exists
        customerProfileRepository.deleteByUserId(userId);
        log.debug("Deleted customer profile for user: userId={}", userId);

        // Delete tokens
        refreshTokenRepository.deleteByUserId(userId);
        log.debug("Deleted refresh tokens for user: userId={}", userId);

        resetPasswordTokenRepository.deleteByUserId(userId);
        log.debug("Deleted password reset tokens for user: userId={}", userId);

        verificationTokenRepository.deleteByUserId(userId);
        log.debug("Deleted verification tokens for user: userId={}", userId);

        // Delete staff invites for this user
        staffInviteTokenRepository.deleteByUserId(userId);
        log.debug("Deleted staff invite tokens for user: userId={}", userId);

        // Delete business memberships
        businessMemberRepository.deleteByUserId(userId);
        log.debug("Deleted business memberships for user: userId={}", userId);

        // Delete the user
        userRepository.deleteById(userId);
        log.info("Successfully deleted user: userId={}", userId);
    }
}
