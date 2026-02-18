package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Dto.Registration.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.Entity.CustomerProfile;
import com.shabanaj.beloyal.Entity.User;

public interface CustomerProfileService {
    CustomerProfile createCustomerPofile(User user, CustomerProfileRegisterDto dto);
    CustomerProfile getCustomerProfileByUser(User user);
}
