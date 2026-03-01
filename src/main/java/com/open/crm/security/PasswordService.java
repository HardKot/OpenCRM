package com.open.crm.security;

import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordService {
    private final PasswordEncoder passwordEncoder;
    private static String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String generatePassword() {
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    
    }

    public PasswordType getPasswordType(String password) {
        int score = 0;

        if (password.length() >= 6) score++;
        if (password.length() >= 10) score++;
        if (password.matches("(?=.*[0-9]).*")) score++;
        if (password.matches("(?=.*[a-z]).*")) score++;
        if (password.matches("(?=.*[A-Z]).*")) score++;

        if (score >= 5) {
            return PasswordType.SIMPLE;
        } else if (score >= 3) {
            return PasswordType.MEDIUM;
        } else {
            return PasswordType.SIMPLE;
        }
    }

    public String passwordHash(String password) {
        return passwordEncoder.encode(password);
    }
    
    public boolean matchPassword(String password, String hash) {
        return passwordEncoder.matches(password, hash);
    }

    public enum PasswordType {
        SIMPLE,
        MEDIUM,
        HARD
    }
}
