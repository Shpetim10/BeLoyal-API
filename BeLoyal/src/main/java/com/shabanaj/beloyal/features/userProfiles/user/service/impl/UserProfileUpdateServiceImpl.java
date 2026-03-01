package com.shabanaj.beloyal.features.userProfiles.user.service.impl;

import com.shabanaj.beloyal.common.Exception.BadRequestException;
import com.shabanaj.beloyal.common.Helpers.UserFinder;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.features.userProfiles.user.dto.UpdateUserProfileDto;
import com.shabanaj.beloyal.features.userProfiles.user.service.UserProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.shabanaj.beloyal.common.Configurations.JsonHelperConfig.*;
import static com.shabanaj.beloyal.common.Configurations.JsonHelperConfig.isPresent;
import static com.shabanaj.beloyal.common.Configurations.JsonHelperConfig.normalizeOptional;

@Service
@RequiredArgsConstructor
public class UserProfileUpdateServiceImpl implements UserProfileUpdateService {
    private final UserRepository userRepository;
    private final UserFinder userFinder;

    @Override
    @Transactional
    public void updateUserProfile(UpdateUserProfileDto dto, Long userId) throws BadRequestException {
        User user= userFinder.findByIdOrThrows(userId);

        applyRequiredString(dto.getFirstName(), "firstName", user::setFirstName);
        applyRequiredString(dto.getLastName(), "lastName", user::setLastName);
        applyRequiredString(dto.getUsername(), "username", user::setUsername);
        applyOptionalString(dto.getPhoneNumber(), user::setPhoneNumber);

        boolean pathSent = isPresent(dto.getImagePath());
        boolean keySent  = isPresent(dto.getImageKey());

        if (pathSent || keySent) {

            // must send both if updating/clearing image
            if (!(pathSent && keySent)) {
                throw new BadRequestException("logoPath and logoKey must be provided together");
            }

            String newPath = normalizeOptional(dto.getImagePath()); // null => clear, blank => null
            String newKey  = normalizeOptional(dto.getImageKey());

            if ((newPath == null) != (newKey == null)) {
                throw new BadRequestException("logoPath and logoKey must be set together or cleared together");
            }

            user.setProfileImage(newPath);
            user.setProfileImageKey(newKey);
        }

        userRepository.save(user);
    }
}
