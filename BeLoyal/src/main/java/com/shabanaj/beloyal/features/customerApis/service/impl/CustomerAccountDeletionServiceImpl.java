package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.customerApis.service.CustomerAccountDeletionService;
import com.shabanaj.beloyal.features.customerCoupon.repository.CustomerCouponRepository;
import com.shabanaj.beloyal.features.loyaltyCard.repository.LoyaltyCardRepository;
import com.shabanaj.beloyal.features.pointsBucket.repository.PointsBucketRepository;
import com.shabanaj.beloyal.features.pointsBucketConsumption.repository.PointsBucketConsumptionRepository;
import com.shabanaj.beloyal.features.pointsTransaction.repository.PointsTransactionRepository;
import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerAccountDeletionServiceImpl implements CustomerAccountDeletionService {

    private final CustomerProfileRepository customerProfileRepository;
    private final LoyaltyCardRepository loyaltyCardRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final PointsBucketConsumptionRepository pointsBucketConsumptionRepository;
    private final PointsBucketRepository pointsBucketRepository;
    private final PointsTransactionRepository pointsTransactionRepository;
    private final CustomerCouponRepository customerCouponRepository;

    @Override
    @Transactional
    public void deleteCustomerAccount(Long userId) {
        CustomerProfile customerProfile = customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));

        Long customerProfileId = customerProfile.getId();

        // Delete in reverse order of FK dependencies
        // 1. Points bucket consumption depends on points transaction and bucket
        pointsBucketConsumptionRepository.deleteByCustomerProfileId(customerProfileId);

        // 2. Points transactions and buckets
        pointsTransactionRepository.deleteByCustomerProfileId(customerProfileId);
        pointsBucketRepository.deleteByCustomerProfileId(customerProfileId);

        // 3. Customer coupons (redemptions)
        customerCouponRepository.deleteByCustomerProfileId(customerProfileId);

        // 4. Loyalty cards
        loyaltyCardRepository.deleteByCustomerProfileId(customerProfileId);

        // 5. Loyalty accounts
        loyaltyAccountRepository.deleteByCustomerProfileId(customerProfileId);

        // 6. Customer profile
        customerProfileRepository.deleteByUserId(userId);

        // User record is kept to preserve business membership data and business history
    }
}
