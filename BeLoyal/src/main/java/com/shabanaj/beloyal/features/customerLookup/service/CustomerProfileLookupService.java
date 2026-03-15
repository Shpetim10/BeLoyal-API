package com.shabanaj.beloyal.features.customerLookup.service;

import com.shabanaj.beloyal.model.Entity.CustomerProfile;

public interface CustomerProfileLookupService {
    CustomerProfile search(String query);
}
