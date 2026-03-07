package com.open.crm.admin.application;

import java.util.List;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Component;

import com.open.crm.admin.application.exceptions.TenantException;
import com.open.crm.admin.application.interfaces.IDatabase;
import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.admin.entities.user.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TenantService {
    private final ITenantRepository tenantRepository;
    private final IDatabase database;
    private final UserService userService;

    public Tenant createTenant(String email) throws TenantException {
        try {
            Tenant tenant = new Tenant();
            tenant.setActive(true);
            tenant.setReady(false);

            tenantRepository.save(tenant);

            database.copySchema(
                    database.getTemplateTenantSchemaName(),
                    tenant.getSchemaName());
            database.setContextTenant(tenant);

            User user = new User();
            user.setUsername(email);
            userService.createUser(user);

            return tenant;

        } catch (Exception e) {
            throw new TenantException("Error creating tenant: " + e.getMessage());
        }
    }
}
