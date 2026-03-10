package com.open.crm.tenancy;

import java.util.Objects;
import java.util.UUID;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

import com.open.crm.admin.entities.tenant.Tenant;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<UUID> {

    private static final String DEFAULT_TENANT = "00000000-0000-0000-0000-000000000000";

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        Tenant tenant = TenantContext.getCurrentTenant();

        if (Objects.isNull(tenant)) {
            return UUID.fromString(DEFAULT_TENANT);
        }

        return tenant.getId();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

}