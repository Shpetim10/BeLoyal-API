package com.shabanaj.beloyal.features.userProfiles.user.service.impl;

import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.features.user.service.UserService;
import com.shabanaj.beloyal.features.userProfiles.user.dto.UserDetailsDto;
import com.shabanaj.beloyal.features.userProfiles.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserService userService;

    @Override
    public UserDetailsDto getUserProfile(Long userId) {
        User user=userService.getUserOrThrow(userId);

        return new UserDetailsDto(user);
    }
}
