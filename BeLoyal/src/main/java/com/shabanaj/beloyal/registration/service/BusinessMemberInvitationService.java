package com.shabanaj.beloyal.registration.service;

import com.shabanaj.beloyal.registration.dto.businessMemberRegistration.BusinessMemberInviteDto;

public interface BusinessMemberInvitationService {
    void invite(BusinessMemberInviteDto dto, Long businessId);
}
