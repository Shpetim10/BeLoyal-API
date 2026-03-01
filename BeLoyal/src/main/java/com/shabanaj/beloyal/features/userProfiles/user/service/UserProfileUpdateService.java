package com.shabanaj.beloyal.features.userProfiles.user.service;

import com.shabanaj.beloyal.features.userProfiles.user.dto.UpdateUserProfileDto;

public interface UserProfileUpdateService {
    void updateUserProfile(UpdateUserProfileDto dto, Long userId);
}
