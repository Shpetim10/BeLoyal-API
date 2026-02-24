package com.shabanaj.beloyal.userProfiles.customer.service;

import com.shabanaj.beloyal.userProfiles.customer.dto.CustomerProfileUpdateDto;

public interface CustomerProfileUpdateService {
    void updateCustomerProfile(CustomerProfileUpdateDto dto, Long userId) ;
}
