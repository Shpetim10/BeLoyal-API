package com.shabanaj.beloyal.features.userProfiles.customer.service;

import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.User;

public interface CustomerProfileService {
    CustomerProfile createCustomerPofile(User user, CustomerProfileRegisterDto dto);
    CustomerProfile getCustomerProfileByUser(User user);
    CustomerProfile getCustomerProfileById(Long customerProfileId);
}
