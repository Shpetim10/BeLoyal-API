package com.shabanaj.beloyal.features.loyaltyAccount.service;

import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import com.shabanaj.beloyal.model.Entity.LoyaltyCard;

import java.util.List;

public interface LoyaltyAccountService {
    LoyaltyAccount save(LoyaltyAccount loyaltyAccount);
    LoyaltyAccount getLoyaltyAccountByCustomerProfileAndBusiness(CustomerProfile customerProfile, Business business);
    LoyaltyAccount findLoyaltyAccountOrCreate(CustomerProfile customerProfile, Business business);
    LoyaltyAccount findLoyaltyAccountByCustomerProfileId(Long customerProfileId);
    List<LoyaltyAccount> findLoyaltyAccountsByCustomerProfile(CustomerProfile customerProfile);
}
