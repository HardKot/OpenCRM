package com.open.crm.components.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Component;

import com.open.crm.root.application.interfaces.IDatabase;
import com.open.crm.root.entities.tenant.Tenant;
import com.open.crm.tenancy.TenantContext;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Database implements IDatabase {
    private final DataSource dataSource;
    private final String templateTenantSchemaName = "template_schema_tenant";
    private final CopyDatabaseSchema copyDatabaseSchema;

    @Override
    public String getTemplateTenantSchemaName() {
        return templateTenantSchemaName;
    }

    @Override
    public void copySchema(String from, String to) throws Exception {
        copyDatabaseSchema.execute(from, to);
    }

    @Override
    public void setContextTenant(Tenant tenant) {
        TenantContext.setCurrentTenantSchemaName(tenant.getSchemaName());
    }

    @Override
    public void clearContextTenant() {
        TenantContext.clear();
    }

    @Override
    public void schemaChangeTenant(String schema, Tenant tenant) throws Exception {

    }

    @PostConstruct
    public void runMigration() {
        try {
            runMigrationRoot();
            runMigrationTenant(templateTenantSchemaName);

            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet tenantSchemas = stmt.executeQuery("SELECT schema_name FROM public.tenants");
            while (tenantSchemas.next()) {
                String schemaName = tenantSchemas.getString("schema_name");
                runMigrationTenant(schemaName);
            }
        } catch (Exception e) {
            log.error("Error during database migration: {}", e.getMessage());
        }
    }

    public void runMigrationRoot() {
        Flyway.configure()
                .dataSource(dataSource)
                .schemas("public")
                .locations("classpath:migrations/root")
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .load()
                .migrate();
    }

    public void runMigrationTenant(String schemaName) {
        Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .locations("classpath:migrations/tenant")
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .load()
                .migrate();
    }

}
