package com.shabanaj.beloyal.registration.service;

import com.shabanaj.beloyal.registration.dto.businessMemberRegistration.BusinessMemberRegisterDto;

public interface BusinessMemberAcceptanceService {
    void registerNewUserAsBusinessMember(BusinessMemberRegisterDto dto);
    void assignExistingUserToBusiness(String token, Long authenticatedUserId);
}
