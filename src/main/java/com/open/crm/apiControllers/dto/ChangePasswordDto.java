package com.open.crm.apiControllers.dto;

public record ChangePasswordDto(String password, String newPassword, String confirmPassword) {}
