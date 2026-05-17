package com.shabanaj.beloyal.features.customerApis.service.impl;

import com.shabanaj.beloyal.features.customerApis.service.CustomerProfileCreationService;
import com.shabanaj.beloyal.features.registration.dto.customerRegistraton.CustomerProfileRegisterDto;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerProfileCreationServiceImpl implements CustomerProfileCreationService {

    private final UserService userService;
    private final CustomerProfileService customerProfileService;

    @Override
    @Transactional
    public CustomerProfile createProfileForUser(Long userId, CustomerProfileRegisterDto dto) {
        User user = userService.getUserOrThrow(userId);
        return customerProfileService.createCustomerPofile(user, dto);
    }
}
