package com.shabanaj.beloyal.features.customerLookup.service.impl;

import com.shabanaj.beloyal.features.business.service.BusinessService;
import com.shabanaj.beloyal.features.customerLookup.dto.CustomerLookupDto;
import com.shabanaj.beloyal.features.customerLookup.service.CustomerLookupForBusinessService;
import com.shabanaj.beloyal.features.customerLookup.service.CustomerProfileLookupService;
import com.shabanaj.beloyal.features.loyaltyAccount.service.LoyaltyAccountService;
import com.shabanaj.beloyal.model.Entity.Business;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.LoyaltyAccount;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("manualCodeLookupForBusiness")
public class CustomerManualCodeLookupForBusinessService implements CustomerLookupForBusinessService {
    private final CustomerProfileLookupService customerProfileLookupService;
    private final BusinessService businessService;
    private final LoyaltyAccountService loyaltyAccountService;

    public CustomerManualCodeLookupForBusinessService(@Qualifier("manualCodeLookup") CustomerProfileLookupService customerProfileLookupService, BusinessService businessService, LoyaltyAccountService loyaltyAccountService) {
        this.customerProfileLookupService = customerProfileLookupService;
        this.businessService = businessService;
        this.loyaltyAccountService = loyaltyAccountService;
    }

    @Override
    public CustomerLookupDto getCustomerForBusiness(Long businessId, String query) {
        // get customer profile
        CustomerProfile customerProfile= customerProfileLookupService.search(query);

        // get business
        Business business= businessService.getBusinessById(businessId);

        // find loyalty account or create for a certain business
        LoyaltyAccount loyaltyAccount= loyaltyAccountService.findLoyaltyAccountOrCreate(customerProfile, business);

        return CustomerLookupDto.fromCustomerProfileAndLoyaltyAccount(
                    customerProfile, loyaltyAccount
        );
    }
}
