package com.shabanaj.beloyal.Service;

import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Enums.Role;

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
    void assignRole(Long userId, Role role);
    void removeRole(Long userId, Role role);
    void changePassword(Long userId, String oldPassword, String password);
    void deleteUser(Long userId);
    void updateUser(Long userId, User updatedUser);
    Optional<User> getUserById(Long userId);
    List<User> getAllUsers();
    void validateUserFields(User user);
    Set<Role> getUserRoles(Long userId);
    User getUserOrThrow(Long userId);
}
