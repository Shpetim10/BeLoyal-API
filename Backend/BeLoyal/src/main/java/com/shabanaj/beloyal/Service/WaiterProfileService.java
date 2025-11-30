package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Dto.Registration.WaiterProfileRegisterDto;
import com.shabanaj.beloyal.Entity.BusinessProfile;
import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Entity.WaiterProfile;

public interface WaiterProfileService {
    WaiterProfile createWaiterProfile(User user, BusinessProfile businessProfile, WaiterProfileRegisterDto dto);
}
