package com.shabanaj.beloyal.features.user.service.impl;

import com.shabanaj.beloyal.features.user.dto.UpdateUserDto;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.model.Enums.UserStatus;
import com.shabanaj.beloyal.common.Exception.UserNotFound;
import com.shabanaj.beloyal.features.user.repository.UserRepository;
import com.shabanaj.beloyal.features.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public void updateLastLogin(Long userId) {
        User user=getUserOrThrow(userId);

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void enableUser(Long userId) {
        User user=getUserOrThrow(userId);

        user.setStatus(UserStatus.ENABLED);
        userRepository.save(user);
    }

    @Override
    public void disableUser(Long userId) {
        User user=getUserOrThrow(userId);

        user.setStatus(UserStatus.DISABLED);
        userRepository.save(user);
    }

    @Override
    public void lockUser(Long userId) {
        User user=getUserOrThrow(userId);

        user.setStatus(UserStatus.LOCKED);
        userRepository.save(user);
    }

    @Override
    public void unlockUser(Long userId) {
        User user=getUserOrThrow(userId);

        user.setStatus(UserStatus.ENABLED);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user=getUserOrThrow(userId);

        userRepository.delete(user);
    }

    @Override
    public void updateUser(Long userId, UpdateUserDto updatedUser) {
        User toUpdate= getUserOrThrow(userId);

        toUpdate.setFirstName(updatedUser.getFirstName());
        toUpdate.setLastName(updatedUser.getLastName());
        toUpdate.setEmail(updatedUser.getEmail());
        toUpdate.setUsername(updatedUser.getUsername());
        toUpdate.setPhoneNumber(updatedUser.getPhoneNumber());
        toUpdate.setProfileImage(updatedUser.getProfileImage());
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Set<Role> getUserRoles(Long userId) {
        User user = getUserOrThrow(userId);

        if( user.getRoles() != null)
            return user.getRoles();
        return Set.of();
    }

    public User getUserOrThrow(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFound());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPasswordHash(
                passwordEncoder.encode(newPassword)
        );
        userRepository.save(user);
    }
}
