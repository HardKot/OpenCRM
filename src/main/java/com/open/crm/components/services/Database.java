package com.open.crm.components.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private final UUID templateTenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final CopyDatabaseSchema copyDatabaseSchema;

    @Override
    public void dropTimestamp(String schema) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            Map<String, List<String>> tableColumns = new java.util.LinkedHashMap<>();

            try (Statement selectStmt = conn.createStatement()) {
                ResultSet rs = selectStmt.executeQuery(String.format(
                        "SELECT table_name, column_name FROM information_schema.columns " +
                                "WHERE table_schema = '%s' AND column_name IN ('created_at', 'updated_at')",
                        schema));

                while (rs.next()) {
                    String tableName = rs.getString(1);
                    String columnName = rs.getString(2);
                    tableColumns.computeIfAbsent(tableName, k -> new ArrayList<>()).add(columnName);
                }
            }

            LocalDateTime now = LocalDateTime.now();
            try (Statement updateStmt = conn.createStatement()) {
                for (Map.Entry<String, List<String>> entry : tableColumns.entrySet()) {
                    String tableName = entry.getKey();
                    String setClauses = entry.getValue().stream()
                            .map(col -> col + " = '" + now + "'")
                            .collect(java.util.stream.Collectors.joining(", "));
                    updateStmt.execute(String.format("UPDATE %s.%s SET %s", schema, tableName, setClauses));
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

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
        TenantContext.setCurrentTenant(tenant);
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
                    updateStmt.execute(String.format("UPDATE %s.%s SET tenant_id = '%s'", schema,
                            tableName, tenant.getId()));
                }
            }
        } catch (Exception e) {
            log.error("Error during schema change for tenant {}: {}", tenant.getId(), e.getMessage());
            throw e;
        }
    }

    @PostConstruct
    public void runMigration() {
        try {
            runMigrationAdmin();
            runMigrationTenant(templateTenantId, templateTenantSchemaName);

            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet tenantSchemas = stmt.executeQuery("SELECT id, schema_name FROM public.tenants");
            while (tenantSchemas.next()) {
                String schemaName = tenantSchemas.getString("schema_name");
                UUID tenantId = UUID.fromString(tenantSchemas.getString("id"));
                runMigrationTenant(tenantId, schemaName);
            }
        } catch (Exception e) {
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

    public void runMigrationTenant(UUID tenantId, String schemaName) {
        Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .locations("classpath:migrations/tenant")
                .placeholders(Map.of("tenantId", tenantId.toString()))
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .load()
                .migrate();
    }

}
