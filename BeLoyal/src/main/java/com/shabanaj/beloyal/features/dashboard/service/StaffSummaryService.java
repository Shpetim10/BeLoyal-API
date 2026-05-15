package com.shabanaj.beloyal.features.dashboard.service;

import com.shabanaj.beloyal.features.dashboard.dto.StaffSummaryDto;

public interface StaffSummaryService {
    StaffSummaryDto getSummary(Long businessId, Long staffUserId);
}
