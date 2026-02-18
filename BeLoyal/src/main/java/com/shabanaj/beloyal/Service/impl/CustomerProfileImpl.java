package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Dto.Registration.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.Entity.CustomerProfile;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Exception.CustomerProfileExistsException;
import com.shabanaj.beloyal.Exception.UserNotFound;
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
        if(user==null || userRepository.findUserByEmail(user.getEmail())==null){
            throw new UserNotFound("Your user account could not be found!");
        }

        if(customerProfileRepository.findByUser(user).isPresent()){
            throw new CustomerProfileExistsException();
        }

        CustomerProfile customerProfile = new CustomerProfile();

        //set attributes
        customerProfile.setBirthDate(dto.getBirthdate());
        customerProfile.setCity(dto.getCity());
        customerProfile.setCountry(dto.getCountry());
        customerProfile.setGender(dto.getGender());
        customerProfile.setNotificationEnabled(dto.isNotificationEnabled());
        customerProfile.setReferredBy(dto.getReferredBy());
        user.setProfileImage(dto.getProfileImagePath());

        //generate referral code
        String referralCode= referralCodeGenerator.generateReferralCode();
        customerProfile.setReferralCode(referralCode);

        // Assign to user
        customerProfile.setUser(user);
        // Save
        userRepository.save(user);
        return customerProfileRepository.save(customerProfile);
    }

    @Override
    public CustomerProfile getCustomerProfileByUser(User user) {
        return customerProfileRepository.findByUser(user).orElseThrow(()-> new RuntimeException("Customer profile not found"));
    }
}
