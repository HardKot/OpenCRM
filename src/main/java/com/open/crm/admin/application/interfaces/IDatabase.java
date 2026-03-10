package com.open.crm.admin.application.interfaces;

import com.open.crm.admin.entities.tenant.Tenant;

public interface IDatabase {

    void copySchema(String from, String to) throws Exception;

    void schemaChangeTenant(String schema, Tenant tenant) throws Exception;

    void setValue(String table, String query, Tenant tenant) throws Exception;

    void dropTimestamp(String schema) throws Exception;

    String getTemplateTenantSchemaName();

    void setContextTenant(Tenant tenant);

    void clearContextTenant();

}
