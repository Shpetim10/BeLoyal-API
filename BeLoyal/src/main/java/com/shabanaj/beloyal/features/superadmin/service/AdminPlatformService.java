package com.shabanaj.beloyal.features.superadmin.service;

import com.shabanaj.beloyal.features.superadmin.dto.PlatformUserSummaryDto;

import java.util.List;

public interface AdminPlatformService {
    List<PlatformUserSummaryDto> getAllUsers();
}
