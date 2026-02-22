package com.shabanaj.beloyal.userProfiles.customer.service;

import com.shabanaj.beloyal.registration.dto.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.User;

public interface CustomerProfileService {
    CustomerProfile createCustomerPofile(User user, CustomerProfileRegisterDto dto);
    CustomerProfile getCustomerProfileByUser(User user);
}
