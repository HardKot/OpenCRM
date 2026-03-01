package com.open.crm.components.gateways;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.open.crm.core.application.UseCreateTenant;
import com.open.crm.core.application.repositories.ITenantRepository;
import com.open.crm.core.application.repositories.IUserRepository;
import com.open.crm.components.events.ApplicationEmailEvent;
import com.open.crm.core.events.NotificateEmail;
import com.open.crm.security.PasswordService;
import com.open.crm.security.User;
import com.open.crm.tenancy.Tenant;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class UseCreateTenantGateway implements UseCreateTenant.Gateway {
    private final IUserRepository userRepository;
    private final ITenantRepository tenantRepository;
    private final PasswordService passwordService;
    private final ApplicationEventPublisher applicationEventPublisher;


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
    public void sendEmail(NotificateEmail emailModel) {
        ApplicationEvent event = new ApplicationEmailEvent(this, emailModel.email(), emailModel.subject(), emailModel.templateName(), emailModel.context());

        applicationEventPublisher.publishEvent(event);
    }
    
    @Override
    public void saveTenant(Tenant tenant) {
        // tenantRepository.save(tenant);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
