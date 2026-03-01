package com.shabanaj.beloyal.features.userProfiles.staff.service;

import com.shabanaj.beloyal.features.userProfiles.staff.dto.BusinessMemberDetailsDto;

public interface BusinessMemberProfileService {
    BusinessMemberDetailsDto getStaffProfileDetails(Long userId, Long businessId);
}
