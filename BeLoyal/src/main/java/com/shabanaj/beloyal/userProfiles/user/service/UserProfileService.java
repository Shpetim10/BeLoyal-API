package com.shabanaj.beloyal.userProfiles.user.service;

import com.shabanaj.beloyal.userProfiles.user.dto.UserDetailsDto;

public interface UserProfileService {
    UserDetailsDto getUserProfile(Long userId);
}
