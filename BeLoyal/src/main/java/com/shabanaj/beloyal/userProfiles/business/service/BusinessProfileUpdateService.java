package com.shabanaj.beloyal.userProfiles.business.service;

import com.shabanaj.beloyal.userProfiles.business.dto.BusinessProfileUpdateDto;

public interface BusinessProfileUpdateService {
    void updateBusinessProfile(BusinessProfileUpdateDto dto, Long businessId);
}
