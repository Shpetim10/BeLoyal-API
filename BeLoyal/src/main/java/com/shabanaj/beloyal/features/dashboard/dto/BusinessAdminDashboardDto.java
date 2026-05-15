package com.shabanaj.beloyal.features.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BusinessAdminDashboardDto {
    private long staffCount;
    private long activeCouponsCount;
    private long transactionsTotal;
    private long loyalCustomersCount;
    private long todayPointsIssued;
}
