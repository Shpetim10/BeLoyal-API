package com.shabanaj.beloyal.features.customerApis.service;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerHomeResponse;

public interface CustomerHomeService {
    CustomerHomeResponse getHome(Long userId);
}
