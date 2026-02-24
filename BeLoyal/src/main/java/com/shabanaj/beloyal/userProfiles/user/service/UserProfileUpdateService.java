package com.shabanaj.beloyal.userProfiles.user.service;

import com.shabanaj.beloyal.userProfiles.user.dto.UpdateUserProfileDto;

public interface UserProfileUpdateService {
    void updateUserProfile(UpdateUserProfileDto dto, Long userId);
}
