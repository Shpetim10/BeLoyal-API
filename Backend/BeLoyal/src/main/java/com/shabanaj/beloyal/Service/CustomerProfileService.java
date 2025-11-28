package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Dto.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.Entity.CustomerProfile;
import com.shabanaj.beloyal.Entity.User;

public interface CustomerProfileService {
    CustomerProfile createCustomerPofile(User user, CustomerProfileRegisterDto dto);
}
