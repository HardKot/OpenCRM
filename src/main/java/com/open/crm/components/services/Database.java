package com.open.crm.components.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Component;

import com.open.crm.admin.application.interfaces.IDatabase;
import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.tenancy.TenantContext;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Database implements IDatabase {

    private final DataSource dataSource;

    private final String templateTenantSchemaName = "tenant_template";

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
        try (Connection conn = dataSource.getConnection()) {
            List<String> tables = new ArrayList<>();

            try (Statement selectStmt = conn.createStatement()) {
                ResultSet tablesResultSet = selectStmt.executeQuery(String.format(
                        "SELECT table_name FROM information_schema.columns WHERE table_schema = '%s' AND column_name = 'tenant_id'",
                        schema));

                while (tablesResultSet.next()) {
                    tables.add(tablesResultSet.getString(1));
                }
            }

            try (Statement updateStmt = conn.createStatement()) {
                for (String tableName : tables) {
                    updateStmt.execute(String.format("UPDATE %s.%s SET tenant_id = '%s' WHERE tenant_id = '%s'", schema,
                            tableName, tenant.getSchemaName(), templateTenantSchemaName));
                }
            }
        }
        catch (Exception e) {
            log.error("Error during schema change for tenant {}: {}", tenant.getId(), e.getMessage());
            throw e;
        }
    }

    @PostConstruct
    public void runMigration() {
        try {
            runMigrationAdmin();
            runMigrationTenant(templateTenantSchemaName);

            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet tenantSchemas = stmt.executeQuery("SELECT schema_name FROM public.tenants");
            while (tenantSchemas.next()) {
                String schemaName = tenantSchemas.getString("schema_name");
                runMigrationTenant(schemaName);
            }
        }
        catch (Exception e) {
            log.error("Error during database migration: {}", e.getMessage());
        }
    }

    public void runMigrationAdmin() {
        Flyway.configure()
            .dataSource(dataSource)
            .schemas("public")
            .locations("classpath:migrations/admin")
            .placeholders(Map.of("template_schema_name", templateTenantSchemaName))
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
            .placeholders(Map.of("tenantId", schemaName))
            .baselineOnMigrate(true)
            .validateOnMigrate(true)
            .load()
            .migrate();
    }

}
