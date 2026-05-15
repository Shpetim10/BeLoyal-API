package com.shabanaj.beloyal.features.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffSummaryDto {
    private long todayScansCount;
    private long pendingRedemptionsCount;
    private long transactionsCount;
    private long activeCustomersCount;
}
