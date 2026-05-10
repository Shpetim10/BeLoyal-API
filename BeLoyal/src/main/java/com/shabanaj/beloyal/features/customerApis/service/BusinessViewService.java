package com.shabanaj.beloyal.features.customerApis.service;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerBusinessDto;

import java.util.List;

public interface BusinessViewService {
    List<CustomerBusinessDto> getBusinessesForCustomer(Long userId);
}
