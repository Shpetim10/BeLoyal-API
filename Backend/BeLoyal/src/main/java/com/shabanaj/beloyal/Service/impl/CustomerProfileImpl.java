package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.CustomerProfileRegisterDto;
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
        //Complete other fields from DTO
        CustomerProfile customerProfile= new CustomerProfile();
        customerProfile.setBirthDate(dto.getBirthdate());
        customerProfile.setGender(dto.getGender());
        customerProfile.setCity(dto.getCity());
        customerProfile.setCountry(dto.getCountry());
        customerProfile.setNotificationEnabled(dto.isNotificationEnabled());
        //Generate referral code
        String referralCode= referralCodeGenerator.generateReferralCode();
        customerProfile.setReferralCode(referralCode);

        return customerProfileRepository.save(customerProfile);
    }
}
