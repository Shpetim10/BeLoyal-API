package com.shabanaj.beloyal.features.userProfiles.customer.service.impl;

import com.shabanaj.beloyal.common.Helpers.UserFinder;
import com.shabanaj.beloyal.model.Entity.CustomerProfile;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.features.userProfiles.customer.dto.CustomerProfileUpdateDto;
import com.shabanaj.beloyal.features.userProfiles.customer.repository.CustomerProfileRepository;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileService;
import com.shabanaj.beloyal.features.userProfiles.customer.service.CustomerProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.shabanaj.beloyal.common.Configurations.JsonHelperConfig.*;

@Service
@RequiredArgsConstructor
public class CustomerProfileUpdateServiceImpl implements CustomerProfileUpdateService {
    private final UserFinder userFinder;
    private final CustomerProfileRepository customerProfileRepository;
    private final CustomerProfileService customerProfileService;

    @Override
    @Transactional
    public void updateCustomerProfile(CustomerProfileUpdateDto dto, Long userId){
        if(dto==null){
            return;
        }

        //Find user and his profile
        User user= userFinder.findByIdOrThrows(userId);
        CustomerProfile customerProfile= customerProfileService.getCustomerProfileByUser(user);

        // Update logic
        applyOptionalString(dto.getCity(),  customerProfile::setCity);
        applyOptionalString(dto.getCountry(),  customerProfile::setCountry);
        applyOptionalEnum(dto.getGender(),  customerProfile::setGender);
        applyRequiredBoolean(dto.getNotificationEnabled(), "notificationEnabled" ,customerProfile::setNotificationEnabled);

        customerProfileRepository.save(customerProfile);
    }
}
