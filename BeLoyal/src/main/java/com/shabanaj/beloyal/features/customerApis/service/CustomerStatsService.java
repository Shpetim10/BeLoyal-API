package com.shabanaj.beloyal.features.customerApis.service;

import com.shabanaj.beloyal.features.customerApis.dto.CustomerSummaryDto;

public interface CustomerStatsService {
    CustomerSummaryDto getCustomerStats(Long userId);
}
