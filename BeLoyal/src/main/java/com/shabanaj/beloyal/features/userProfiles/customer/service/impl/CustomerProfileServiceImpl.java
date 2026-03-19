package com.shabanaj.beloyal.features.userProfiles.customer.service.impl;

import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.common.Exception.CustomerProfileExistsException;
import com.shabanaj.beloyal.common.Exception.UserNotFound;
import com.shabanaj.beloyal.common.Helpers.ReferralCodeGenerator;
import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerProfileServiceImpl implements CustomerProfileService {
    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ReferralCodeGenerator referralCodeGenerator;

    public CustomerProfileServiceImpl(UserRepository userRepository,
            CustomerProfileRepository customerProfileRepository, ReferralCodeGenerator referralCodeGenerator) {
        this.userRepository = userRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.referralCodeGenerator = referralCodeGenerator;
    }

    @Override
    @Transactional
    public CustomerProfile createCustomerPofile(User user, CustomerProfileRegisterDto dto) {
        if (user == null || userRepository.findUserByEmail(user.getEmail()) == null) {
            throw new UserNotFound("Your user account could not be found!");
        }

        if (customerProfileRepository.findByUser(user).isPresent()) {
            throw new CustomerProfileExistsException();
        }

        CustomerProfile customerProfile = new CustomerProfile();

        // set attributes
        customerProfile.setBirthDate(dto.getBirthdate());
        customerProfile.setCity(dto.getCity());
        customerProfile.setCountry(dto.getCountry());
        customerProfile.setGender(dto.getGender());
        customerProfile.setNotificationEnabled(dto.isNotificationEnabled());
        customerProfile.setReferredBy(dto.getReferredBy());
        user.setProfileImage(dto.getProfileImageUrl());
        user.setProfileImageKey(dto.getProfileImageKey());

        // generate referral code
        String referralCode = referralCodeGenerator.generateReferralCode();
        customerProfile.setReferralCode(referralCode);

        // Assign to user
        customerProfile.setUser(user);

        // Ensure user gets the CUSTOMER role upon profile completion
        if (!user.getRoles().contains(com.shabanaj.beloyal.model.Enums.Role.CUSTOMER)) {
            user.getRoles().add(com.shabanaj.beloyal.model.Enums.Role.CUSTOMER);
        }
        // Save
        userRepository.save(user);
        return customerProfileRepository.save(customerProfile);
    }

    @Override
    public CustomerProfile getCustomerProfileByUser(User user) {
        return customerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));
    }

    @Override
    public CustomerProfile getCustomerProfileById(Long customerProfileId) {
        Optional<CustomerProfile> customerProfile = customerProfileRepository.findById(customerProfileId);

        if(customerProfile.isEmpty()){
            throw new UserNotFound("Customer profile not found!");
        }

        return customerProfile.get();
    }
}
