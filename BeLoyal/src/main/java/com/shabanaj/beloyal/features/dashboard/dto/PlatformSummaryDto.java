package com.shabanaj.beloyal.features.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlatformSummaryDto {
    private long totalBusinesses;
    private long activeBusinesses;
    private long pendingApplicationsCount;
    private long registeredUsersCount;
    private AppHealthDto health;

    @Getter
    @Builder
    public static class AppHealthDto {
        private String status;
        private String database;
        private String redis;
        private String diskSpace;
    }
}
