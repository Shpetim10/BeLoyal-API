package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Dto.Registration.BusinessProfileRegisterDto;
import com.shabanaj.beloyal.Entity.BusinessProfile;
import com.shabanaj.beloyal.Entity.User;

public interface BusinessProfileService {
    BusinessProfile createBusinessProfile(User user, BusinessProfileRegisterDto dto);
}
