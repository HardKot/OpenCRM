package com.open.crm.tenancy;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MultiTenantConfig {
    private final DataSource dataSource;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        MultiTenantConnectionProvider connectionProvider,
        CurrentTenantIdentifierResolver tenantResolver
    ) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.open.crm.domain");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.multiTenancy", "SCHEMA");
        props.put("hibernate.multi_tenant_connection_provider", connectionProvider);
        props.put("hibernate.multi_tenant_identifier_resolver", tenantResolver);
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.default_schema", "public");
        props.put("hibernate.show_sql", true);
        
        emf.setJpaPropertyMap(props);
        return emf;
    }
}
