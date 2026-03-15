package com.shabanaj.beloyal.features.customerLookup.service;

import com.shabanaj.beloyal.features.customerLookup.dto.CustomerLookupDto;

public interface CustomerLookupForBusinessService {
    CustomerLookupDto getCustomerForBusiness(Long businessId, String query);
}
