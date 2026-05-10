package com.shabanaj.beloyal.features.loyaltyCard.service.impl;

import com.shabanaj.beloyal.features.loyaltyAccount.service.LoyaltyAccountService;
import com.shabanaj.beloyal.features.loyaltyCard.dto.CustomerPointsSummary;
import com.shabanaj.beloyal.features.loyaltyCard.service.CustomerPointsCalculatorService;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class CustomerPointsCalculatorServiceImpl implements CustomerPointsCalculatorService {
    private final LoyaltyAccountService loyaltyAccountService;

    @Override
    @Transactional
    public Integer getCustomersCurrentPoints(CustomerProfile customerProfile) {
        validateArguments(customerProfile);

        // find active accounts
        List<LoyaltyAccount> loyaltyAccounts= loyaltyAccountService.findLoyaltyAccountsByCustomerProfile(customerProfile);

        AtomicReference<Integer> currentPoints= new AtomicReference<>(0);

        loyaltyAccounts.forEach(loyaltyAccount -> currentPoints.updateAndGet(v -> v + loyaltyAccount.getAvailablePoints())
        );

        return currentPoints.get();
    }

    @Override
    public Integer getLifetimePoints(CustomerProfile customerProfile) {
        validateArguments(customerProfile);

        List<LoyaltyAccount> loyaltyAccounts= loyaltyAccountService.findLoyaltyAccountsByCustomerProfile(customerProfile);

        AtomicReference<Integer> lifetimePoints= new AtomicReference<>(0);

        loyaltyAccounts.forEach(e->lifetimePoints.updateAndGet(v -> v + e.getLifetimeEarned()));

        return lifetimePoints.get();
    }

    @Override
    public Integer getLifetimeSpentPoints(CustomerProfile customerProfile) {
        validateArguments(customerProfile);

        List<LoyaltyAccount> loyaltyAccounts= loyaltyAccountService.findLoyaltyAccountsByCustomerProfile(customerProfile);
        AtomicReference<Integer> lifetimeSpentPoints= new AtomicReference<>(0);

        loyaltyAccounts.forEach(e-> lifetimeSpentPoints.updateAndGet(v -> v + e.getLifetimeRedeemed()));

        return lifetimeSpentPoints.get();
    }

    @Override
    @Transactional
    public CustomerPointsSummary getCustomerPointsSummary(CustomerProfile customerProfile) {
        validateArguments(customerProfile);

        List<LoyaltyAccount> loyaltyAccounts= loyaltyAccountService.findLoyaltyAccountsByCustomerProfile(customerProfile);

        AtomicReference<Integer> currentPoints= new AtomicReference<>(0);
        AtomicReference<Integer> lifetimePoints= new AtomicReference<>(0);
        AtomicReference<Integer> lifetimeSpentPoints= new AtomicReference<>(0);

        loyaltyAccounts.forEach(e->{
            currentPoints.updateAndGet(v -> v + e.getAvailablePoints());
            lifetimePoints.updateAndGet(v -> v + e.getLifetimeEarned());
            lifetimeSpentPoints.updateAndGet(v -> v+e.getLifetimeRedeemed());
        });

        return new CustomerPointsSummary(
                currentPoints.get(),
                lifetimePoints.get(),
                lifetimeSpentPoints.get(),
                loyaltyAccounts.size()
        );
    }

    private void validateArguments(CustomerProfile customerProfile){
        if(customerProfile == null){
            throw new IllegalArgumentException("Customer Profile is null");
        }
    }
}
