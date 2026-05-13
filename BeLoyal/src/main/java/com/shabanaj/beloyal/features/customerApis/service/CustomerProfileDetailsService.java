package com.shabanaj.beloyal.features.customerApis.service;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerProfileDetailsResponse;

public interface CustomerProfileDetailsService {
    CustomerProfileDetailsResponse getProfileDetails(Long userId);
}
