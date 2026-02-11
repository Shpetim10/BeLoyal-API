package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.Registration.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.Entity.CustomerProfile;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Helpers.ReferralCodeGenerator;
import com.shabanaj.beloyal.Repository.CustomerProfileRepository;
import com.shabanaj.beloyal.Repository.UserRepository;
import com.shabanaj.beloyal.Service.CustomerProfileService;
import org.springframework.stereotype.Service;

@Service
public class CustomerProfileImpl implements CustomerProfileService {
    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ReferralCodeGenerator referralCodeGenerator;

    public CustomerProfileImpl(UserRepository userRepository, CustomerProfileRepository customerProfileRepository, ReferralCodeGenerator referralCodeGenerator) {
        this.userRepository = userRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.referralCodeGenerator = referralCodeGenerator;
    }

    @Override
    public CustomerProfile createCustomerPofile(User user, CustomerProfileRegisterDto dto) {
        //TODO
        return null;
    }

    @Override
    public CustomerProfile getCustomerProfileByUser(User user) {
        return customerProfileRepository.findByUser(user).orElseThrow(()-> new RuntimeException("Customer profile not found"));
    }
}
