package com.open.crm.tenancy;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.open.crm.core.application.ITenancyDatasource;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenancyDatasource implements ITenancyDatasource {
    private final JdbcTemplate jdbc;
    private final DataSource dataSource;


    @Transactional
    @Override
    public void createSchemaForTenant(Tenant tenant) {
        String tenantSchema = tenant.getSchemaName();

        jdbc.execute("CREATE SCHEMA IF NOT EXISTS " + tenantSchema);
        
        jdbc.update("UPDATE public.tenants SET active = true, is_ready = true WHERE tenant_id = ?", tenant.getId());

        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .schemas(tenantSchema)
            .locations("classpath:migrations/tenant")
            .baselineOnMigrate(true)
            .validateOnMigrate(true)
            .load();

        flyway.migrate();

        Connection con = DataSourceUtils.getConnection(dataSource);
        try (Statement st = con.createStatement()) {
            st.execute("SET search_path TO " + tenantSchema + ", public");
        } catch (Exception e) {
            throw new RuntimeException("Error creating tenant schema: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
            
    }
}
