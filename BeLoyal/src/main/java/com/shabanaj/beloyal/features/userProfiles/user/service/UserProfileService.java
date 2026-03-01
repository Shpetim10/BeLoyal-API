package com.shabanaj.beloyal.features.userProfiles.user.service;

import com.shabanaj.beloyal.features.userProfiles.user.dto.UserDetailsDto;

public interface UserProfileService {
    UserDetailsDto getUserProfile(Long userId);
}
