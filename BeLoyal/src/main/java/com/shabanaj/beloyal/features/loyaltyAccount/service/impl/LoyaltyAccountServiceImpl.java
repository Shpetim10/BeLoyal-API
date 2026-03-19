package com.shabanaj.beloyal.features.loyaltyAccount.service.impl;

import com.shabanaj.beloyal.common.Exception.LoyaltyAccountNotFound;
import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.loyaltyAccount.repository.LoyaltyAccountRepository;
import com.shabanaj.beloyal.features.loyaltyAccount.service.LoyaltyAccountService;
import com.shabanaj.beloyal.features.loyaltyCard.service.LoyaltyCardService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoyaltyAccountServiceImpl implements LoyaltyAccountService {
    private final LoyaltyAccountRepository  loyaltyAccountRepository;
    private final LoyaltyCardService loyaltyCardService;
    private final BusinessService businessService;

    @Override
    public LoyaltyAccount save(LoyaltyAccount loyaltyAccount) {
        return loyaltyAccountRepository.save(loyaltyAccount);
    }

    @Override
    public LoyaltyAccount getLoyaltyAccountByCustomerProfileAndBusiness(CustomerProfile customerProfile, Business business) {
        Optional<LoyaltyAccount> loyaltyAccount = loyaltyAccountRepository.findByCustomerProfileAndBusiness(customerProfile, business);

        if(loyaltyAccount.isEmpty()){
            throw new LoyaltyAccountNotFound("Loyalty Account not found");
        }

        return loyaltyAccount.get();
    }

    @Override
    public LoyaltyAccount findLoyaltyAccountOrCreate(CustomerProfile customerProfile, Business business) {
        LoyaltyAccount loyaltyAccount;
        try{
            loyaltyAccount = getLoyaltyAccountByCustomerProfileAndBusiness(customerProfile, business);
        } catch (LoyaltyAccountNotFound e) {
            loyaltyAccount=new LoyaltyAccount();
            loyaltyAccount.setCustomerProfile(customerProfile);
            loyaltyAccount.setBusiness(business);
            save(loyaltyAccount);
        }
        return loyaltyAccount;
    }

    @Override
    public LoyaltyAccount findLoyaltyAccountByCustomerProfileId(Long customerProfileId) {
        Optional<LoyaltyAccount> loyaltyAccount = loyaltyAccountRepository.findByCustomerProfileId(customerProfileId);

        if(loyaltyAccount.isEmpty()){
            throw new LoyaltyAccountNotFound("Loyalty Account not found");
        }

        return loyaltyAccount.get();
    }
}
