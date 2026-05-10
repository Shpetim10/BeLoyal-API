package com.shabanaj.beloyal.features.customerApis.service;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerBusinessDetailResponse;

public interface CustomerBusinessDetailService {
    CustomerBusinessDetailResponse getBusinessDetail(Long userId, Long businessId);
}
