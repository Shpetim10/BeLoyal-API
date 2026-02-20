package com.shabanaj.beloyal.business.service;

import com.shabanaj.beloyal.model.Enums.BusinessStatus;

public interface BusinessStatusProviderService {
    BusinessStatus getBusinessStatus(Long businessId);
}
