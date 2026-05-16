package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.customerApis.service.CustomerProfileCreationService;
import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerProfileCreationServiceImpl implements CustomerProfileCreationService {

    private final UserRepository userRepository;
    private final CustomerProfileService customerProfileService;

    @Override
    @Transactional
    public CustomerProfile createProfileForCurrentUser(CustomerProfileRegisterDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return customerProfileService.createCustomerPofile(user, dto);
    }
}
