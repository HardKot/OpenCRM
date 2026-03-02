package com.open.crm.tenancy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

/**
 * Configuration for tenant-specific entities (Client, etc.)
 * These entities use multi-tenancy and reside in tenant-specific schemas
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.open.crm.core.application.repositories",
    entityManagerFactoryRef = "tenantEntityManagerFactory",
    transactionManagerRef = "tenantTransactionManager"
)
@RequiredArgsConstructor
public class TenancyEntityManagerConfig {
    private final DataSource dataSource;

    @Bean(name = "tenantEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
        MultiTenantConnectionProvider<UUID> connectionProvider,
        CurrentTenantIdentifierResolver<UUID> tenantResolver
    ) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.open.crm.core.domain");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setPersistenceUnitName("tenantPU");
        
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.multiTenancy", "SCHEMA");
        props.put("hibernate.multi_tenant_connection_provider", connectionProvider);
        props.put("hibernate.tenant_identifier_resolver", tenantResolver);
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.show_sql", true);
        props.put("hibernate.hbm2ddl.auto", "none");
        
        emf.setJpaPropertyMap(props);
        return emf;
    }

    @Bean(name = "tenantTransactionManager")
    public PlatformTransactionManager tenantTransactionManager(
        @Qualifier("tenantEntityManagerFactory") EntityManagerFactory tenantEntityManagerFactory
    ) {
        return new JpaTransactionManager(tenantEntityManagerFactory);
    }
}
