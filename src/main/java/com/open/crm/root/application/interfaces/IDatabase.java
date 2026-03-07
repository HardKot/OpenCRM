package com.open.crm.root.application.interfaces;

import com.open.crm.root.entities.tenant.Tenant;

public interface IDatabase {
    void copySchema(String from, String to) throws Exception;

    void schemaChangeTenant(String schema, Tenant tenant) throws Exception;

    String getTemplateTenantSchemaName();

    void setContextTenant(Tenant tenant);

    void clearContextTenant();
}
