package com.shabanaj.beloyal.features.registration.service;

import com.shabanaj.beloyal.features.registration.dto.businessMemberRegistration.BusinessMemberRegisterDto;

public interface BusinessMemberAcceptanceService {
    void registerNewUserAsBusinessMember(BusinessMemberRegisterDto dto);
    void assignExistingUserToBusiness(String token, Long authenticatedUserId);
}
