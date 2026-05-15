package com.shabanaj.beloyal.features.dashboard.service.impl;

import com.shabanaj.beloyal.features.business.repository.BusinessRepository;
import com.shabanaj.beloyal.features.dashboard.dto.PlatformSummaryDto;
import com.shabanaj.beloyal.features.dashboard.service.PlatformSummaryService;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.model.Enums.BusinessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlatformSummaryServiceImpl implements PlatformSummaryService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final HealthEndpoint healthEndpoint;

    @Override
    public PlatformSummaryDto getSummary() {
        long totalBusinesses = businessRepository.count();
        long activeBusinesses = businessRepository.findAllByBusinessStatus(BusinessStatus.ACTIVE).size();
        long pendingApplicationsCount = businessRepository.findAllByBusinessStatus(BusinessStatus.PENDING_APPROVAL).size();
        long registeredUsersCount = userRepository.count();

        PlatformSummaryDto.AppHealthDto healthDto = resolveHealth();

        return PlatformSummaryDto.builder()
                .totalBusinesses(totalBusinesses)
                .activeBusinesses(activeBusinesses)
                .pendingApplicationsCount(pendingApplicationsCount)
                .registeredUsersCount(registeredUsersCount)
                .health(healthDto)
                .build();
    }

    private PlatformSummaryDto.AppHealthDto resolveHealth() {
        HealthComponent root = healthEndpoint.health();
        String overallStatus = root.getStatus().getCode();

        String database = "UNKNOWN";
        String redis = "UNKNOWN";
        String diskSpace = "UNKNOWN";

        if (root instanceof CompositeHealth composite) {
            Map<String, HealthComponent> components = composite.getComponents();
            if (components != null) {
                database = statusOf(components.get("db"));
                redis = statusOf(components.get("redis"));
                diskSpace = statusOf(components.get("diskSpace"));
            }
        }

        return PlatformSummaryDto.AppHealthDto.builder()
                .status(overallStatus)
                .database(database)
                .redis(redis)
                .diskSpace(diskSpace)
                .build();
    }

    private String statusOf(HealthComponent component) {
        if (component == null) return "UNKNOWN";
        return component.getStatus().getCode();
    }
}
