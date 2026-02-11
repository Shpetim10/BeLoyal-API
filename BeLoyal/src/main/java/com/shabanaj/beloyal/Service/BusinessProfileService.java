package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Dto.Registration.BusinessProfileRegisterDto;
import com.shabanaj.beloyal.Entity.Business;
import com.shabanaj.beloyal.Entity.User;

public interface BusinessProfileService {
    Business createBusinessProfile(User user, BusinessProfileRegisterDto dto);
}
