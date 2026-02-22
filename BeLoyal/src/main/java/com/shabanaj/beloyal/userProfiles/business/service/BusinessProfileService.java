package com.shabanaj.beloyal.userProfiles.business.service;

import com.shabanaj.beloyal.userProfiles.business.dto.BusinessProfileDto;

public interface BusinessProfileService {
    BusinessProfileDto getBusinessProfile(Long businessId);
}
