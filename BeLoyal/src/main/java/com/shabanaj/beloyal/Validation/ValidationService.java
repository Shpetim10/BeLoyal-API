package com.shabanaj.beloyal.Validation;

public interface ValidationService {
    boolean isValidEmail(String email);
    boolean isUniqueEmail(Long userId, String email);
    boolean isValidPassword(String password);
    boolean isValidPhoneNumber(String phoneNumber);
    boolean isUniqueUsername(Long userId, String username);
    boolean isUniquePhoneNumber(Long userId, String phoneNumber);
}
