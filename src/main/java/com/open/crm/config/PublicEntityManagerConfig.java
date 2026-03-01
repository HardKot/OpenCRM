package com.open.crm.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

/**
 * Configuration for public schema entities (Tenant, User)
 * These entities are NOT multi-tenant and always reside in the public schema
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.open.crm.application.repositories.common",
    entityManagerFactoryRef = "publicEntityManagerFactory",
    transactionManagerRef = "publicTransactionManager"
)
@RequiredArgsConstructor
public class PublicEntityManagerConfig {
    
    private final DataSource dataSource;

    @Primary
    @Bean(name = "publicEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean publicEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.open.crm.domain.common");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setPersistenceUnitName("publicPU");
        
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.default_schema", "public");
        props.put("hibernate.show_sql", true);
        props.put("hibernate.hbm2ddl.auto", "update");
        
        emf.setJpaPropertyMap(props);
        return emf;
    }

    @Primary
    @Bean(name = "publicTransactionManager")
    public PlatformTransactionManager publicTransactionManager(
        @Qualifier("publicEntityManagerFactory") EntityManagerFactory publicEntityManagerFactory
    ) {
        return new JpaTransactionManager(publicEntityManagerFactory);
    }
}
