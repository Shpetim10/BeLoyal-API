package com.shabanaj.beloyal.features.dashboard.service;

import com.shabanaj.beloyal.features.dashboard.dto.BusinessAdminDashboardDto;

public interface BusinessAdminDashboardService {
    BusinessAdminDashboardDto getSummary(Long businessId);
}
