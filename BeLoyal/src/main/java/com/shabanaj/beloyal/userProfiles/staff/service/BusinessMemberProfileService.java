package com.shabanaj.beloyal.userProfiles.staff.service;

import com.shabanaj.beloyal.userProfiles.staff.dto.BusinessMemberDetailsDto;

public interface BusinessMemberProfileService {
    BusinessMemberDetailsDto getStaffProfileDetails(Long userId, Long businessId);
}
