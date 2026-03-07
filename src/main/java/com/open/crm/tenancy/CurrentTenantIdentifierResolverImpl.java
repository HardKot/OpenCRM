package com.open.crm.tenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<String> {

    private static final String DEFAULT_TENANT = "default_tenant";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String t = TenantContext.getCurrentTenantSchemaName();
        return (t != null) ? t : DEFAULT_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}