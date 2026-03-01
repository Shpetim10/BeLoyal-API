package com.shabanaj.beloyal.features.userProfiles.customer.service;

import com.shabanaj.beloyal.features.userProfiles.customer.dto.CustomerProfileUpdateDto;

public interface CustomerProfileUpdateService {
    void updateCustomerProfile(CustomerProfileUpdateDto dto, Long userId) ;
}
