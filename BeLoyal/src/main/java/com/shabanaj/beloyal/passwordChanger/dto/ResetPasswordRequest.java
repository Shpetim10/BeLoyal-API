package com.shabanaj.beloyal.passwordChanger.dto;

public record ResetPasswordRequest(String token, String newPassword) {
}
