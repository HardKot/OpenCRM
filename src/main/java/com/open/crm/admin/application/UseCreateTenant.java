package com.open.crm.admin.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.open.crm.admin.application.exceptions.TenantException;
import com.open.crm.admin.application.interfaces.IDatabase;
import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.application.interfaces.IUserRepository;
import com.open.crm.admin.entities.tenant.Tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional("adminTransactionManager")
public class UseCreateTenant {

    private final TenantService tenantService;

    private final ITenantRepository tenantRepository;

    private final UserService userService;

    private final IDatabase database;

    private final IUserRepository userRepository;

    public record Params(String email) {
    }

    public Tenant execute(Params params) throws TenantException {
        if (userRepository.existsByEmail(params.email()))
            throw new TenantException("Email is already taken");

        try {

            Tenant tenant = tenantService.generateTenant();
            userService.createOwnerUser(tenant, params.email(), 1L);

            database.copySchema(database.getTemplateTenantSchemaName(), tenant.getSchemaName());
            database.schemaChangeTenant(tenant.getSchemaName(), tenant);
            database.dropTimestamp(tenant.getSchemaName());
            database.setContextTenant(tenant);

            database.setValue("employees", String.format("email = '%s' WHERE id = 1", params.email()), tenant);

            tenant.setReady(true);
            tenantRepository.save(tenant);

            return tenant;
        }
        catch (Exception e) {
            log.error("Error creating tenant", e);
            throw new TenantException("Error creating tenant: " + e.getMessage());
        }
    }

}
