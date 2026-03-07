package com.open.crm.root.application;

import java.util.List;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Component;

import com.open.crm.root.application.exceptions.TenantException;
import com.open.crm.root.application.interfaces.IDatabase;
import com.open.crm.root.entities.tenant.Tenant;
import com.open.crm.tenancy.ITenantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TenantService {
    private final ITenantRepository tenantRepository;
    private final IDatabase database;

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
            return tenant;

        } catch (Exception e) {
            throw new TenantException("Error creating tenant: " + e.getMessage());
        }
    }
}
