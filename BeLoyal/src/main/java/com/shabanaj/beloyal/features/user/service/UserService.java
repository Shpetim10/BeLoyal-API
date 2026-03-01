package com.shabanaj.beloyal.features.user.service;

import com.shabanaj.beloyal.features.user.dto.UpdateUserDto;
import com.shabanaj.beloyal.model.Entity.User;
import com.shabanaj.beloyal.model.Enums.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserByEmail(String email);
    void updateLastLogin(Long userId);
    void enableUser(Long userId);
    void disableUser(Long userId);
    void lockUser(Long userId);
    void unlockUser(Long userId);
    void deleteUser(Long userId);
    void updateUser(Long userId, UpdateUserDto updatedUser);
    Optional<User> getUserById(Long userId);
    List<User> getAllUsers();
    Set<Role> getUserRoles(Long userId);
    User getUserOrThrow(Long userId);
    void changePassword(User user, String newPassword);
}
