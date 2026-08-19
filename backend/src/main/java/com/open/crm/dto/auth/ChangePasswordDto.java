package com.open.crm.dto.auth;

public record ChangePasswordDto(String password, String newPassword, String confirmPassword) {}
