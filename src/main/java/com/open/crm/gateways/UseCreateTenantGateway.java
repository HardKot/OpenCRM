package com.open.crm.gateways;

import org.springframework.stereotype.Component;

import com.open.crm.application.UseCreateTenant;
import com.open.crm.application.repositories.ITenantRepository;
import com.open.crm.application.repositories.IUserRepository;
import com.open.crm.domain.common.Tenant;
import com.open.crm.domain.common.User;
import com.open.crm.security.PasswordService;
import com.open.crm.services.EmailService;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class UseCreateTenantGateway implements UseCreateTenant.Gateway {
    private final IUserRepository userRepository;
    private final ITenantRepository tenantRepository;
    private final EmailService emailService;
    private final PasswordService passwordService;


    @Override
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public String generatePassword() {
        return passwordService.generatePassword();
    }

    @Override
    public String hashPassword(String password) {
        return passwordService.passwordHash(password);
    }

    @Override
    public boolean sendEmail(String email, String theme, String text) {
        return emailService.sendEmail(email, theme, text);
    }
    
    @Override
    public void saveTenant(Tenant tenant) {
        tenantRepository.save(tenant);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
