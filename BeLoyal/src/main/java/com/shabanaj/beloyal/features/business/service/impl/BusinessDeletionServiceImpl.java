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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessDeletionServiceImpl implements BusinessDeletionService {

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
        // Get members before deletion to check user cleanup
        List<BusinessMember> members = businessMemberRepository.findALlByBusinessId(businessId);

        // Delete in reverse order of FK dependencies
        // 1. Points bucket consumption depends on points transaction and bucket
        pointsBucketConsumptionRepository.deleteByBusinessId(businessId);

        // 2. Points transactions and buckets
        pointsTransactionRepository.deleteByBusinessId(businessId);
        pointsBucketRepository.deleteByBusinessId(businessId);

        // 3. Bill transactions
        billTransactionRepository.deleteByBusinessId(businessId);

        // 4. Customer coupons and coupon details
        customerCouponRepository.deleteByBusinessId(businessId);
        couponDiscountDetailsRepository.deleteByBusinessId(businessId);
        couponFreeProductDetailsRepository.deleteByBusinessId(businessId);
        couponRepository.deleteByBusinessId(businessId);

        // 5. Loyalty accounts (depends on business)
        loyaltyAccountRepository.deleteByBusinessId(businessId);

        // 7. Loyalty and earning settings
        loyaltySettingsRepository.deleteByBusinessId(businessId);
        earningSettingsRepository.deleteByBusinessId(businessId);

        // 8. Catalog items and variants
        catalogItemVariantRepository.deleteByBusinessId(businessId);
        catalogItemRepository.deleteByBusinessId(businessId);
        catalogCategoryRepository.deleteByBusinessId(businessId);

        // 9. Staff invite tokens for this business
        staffInviteTokenRepository.deleteByBusinessId(businessId);

        // 10. Delete business members and handle user cleanup
        for (BusinessMember member : members) {
            Long userId = member.getUser().getId();
            long otherMemberships = businessMemberRepository.countOtherMembershipsForUser(userId, businessId);

            // Delete the membership
            businessMemberRepository.deleteByBusinessId(businessId);

            // Check if user should be deleted
            if (otherMemberships == 0 && !userRepository.findById(userId).get().getRoles().contains(Role.CUSTOMER)) {
                // No other business memberships and not a CUSTOMER, so delete the user
                deleteUser(userId);
            }
        }

        // 11. Delete the business itself
        businessRepository.deleteById(businessId);
    }

    private void deleteUser(Long userId) {
        // Delete customer profile if exists
        customerProfileRepository.deleteByUserId(userId);

        // Delete tokens
        refreshTokenRepository.deleteByUserId(userId);
        resetPasswordTokenRepository.deleteByUserId(userId);
        verificationTokenRepository.deleteByUserId(userId);

        // Delete staff invites for this user
        staffInviteTokenRepository.deleteByUserId(userId);

        // Delete business memberships
        businessMemberRepository.deleteByUserId(userId);

        // Delete the user
        userRepository.deleteById(userId);
    }
}
