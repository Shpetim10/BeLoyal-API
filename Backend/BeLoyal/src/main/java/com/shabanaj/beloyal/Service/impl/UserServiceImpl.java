package com.shabanaj.beloyal.Service.impl;

import com.shabanaj.beloyal.Entity.User;
import com.shabanaj.beloyal.Enums.Role;
import com.shabanaj.beloyal.Exception.UserNotFound;
import com.shabanaj.beloyal.Repository.UserRepository;
import com.shabanaj.beloyal.Service.UserService;
import com.shabanaj.beloyal.Service.ValidationService;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationService validationService;

    public UserServiceImpl(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }


    @Override
    public User createUser(User user) {
        validateUserFields(user);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public void updateLastLogin(Long userId) {
        User user= getUserOrThrow(userId);

        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void enableUser(Long userId) {
        User user= getUserOrThrow(userId);

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableUser(Long userId) {
        User user= getUserOrThrow(userId);

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void lockUser(Long userId) {
        User user= getUserOrThrow(userId);

        user.setLocked(true);
        userRepository.save(user);
    }

    @Override
    public void unlockUser(Long userId) {
        User user = getUserOrThrow(userId);

        user.setLocked(false);
        userRepository.save(user);
    }

    @Override
    public void assignRole(Long userId, Role role) {
        User user = getUserOrThrow(userId);

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRole(Long userId, Role role) {
        User user = getUserOrThrow(userId);

        user.getRoles().remove(role);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId,String oldPassword, String password) {
        User user = getUserOrThrow(userId);

        if(!user.getPassword().equals(oldPassword))
            throw new ValidationException("Old password is incorrect!");

        if (!validationService.isValidPassword(password))
            throw new ValidationException("Invalid password!");

        //change when adding password hashing
        user.setPassword(password);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = getUserOrThrow(userId);

        userRepository.delete(user);
    }

    @Override
    public void updateUser(Long userId, User updatedUser) {
        //find user
        User user = getUserOrThrow(userId);
        //validate
        updatedUser.setId(userId);
        validateUserFields(updatedUser);
        //update fields
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setUsername(updatedUser.getUsername());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setEmail(updatedUser.getEmail());
        user.setRoles(updatedUser.getRoles());
        //save in db
        userRepository.save(user);
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
    public void validateUserFields(User user) {
        if(user==null) return;

        if (!validationService.isValidEmail(user.getEmail()))
            throw new ValidationException("Invalid email!");
        if (!validationService.isUniqueEmail(user.getId(), user.getEmail()))
            throw new ValidationException("Email already exists!");
        if (!validationService.isValidPhoneNumber(user.getPhoneNumber()))
            throw new ValidationException("Invalid phone number!");
        if (!validationService.isUniquePhoneNumber(user.getId(), user.getPhoneNumber()))
            throw new ValidationException("Phone number already exists!");
        if (!validationService.isValidPassword(user.getPassword()))
            throw new ValidationException("Invalid password!");
        if (!validationService.isUniqueUsername(user.getId(), user.getUsername()))
            throw new ValidationException("Username already exists!");
        if (user.getRoles().isEmpty())
            throw new ValidationException("User must have at least one role!");
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
}
