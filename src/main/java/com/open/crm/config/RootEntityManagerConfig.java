package com.open.crm.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
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

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "com.open.crm.tenancy", "com.open.crm.security" },
        entityManagerFactoryRef = "rootEntityManagerFactory", transactionManagerRef = "rootTransactionManager")
@RequiredArgsConstructor
public class RootEntityManagerConfig {

    private final DataSource dataSource;

    @Primary
    @Bean(name = "rootEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean rootEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.open.crm.root");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setPersistenceUnitName("rootPU");

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.default_schema", "public");
        props.put("hibernate.show_sql", true);
        props.put("hibernate.hbm2ddl.auto", "none");

        emf.setJpaPropertyMap(props);
        return emf;
    }

    @Primary
    @Bean(name = "rootTransactionManager")
    public PlatformTransactionManager rootTransactionManager(
            @Qualifier("rootEntityManagerFactory") EntityManagerFactory rootEntityManagerFactory) {
        return new JpaTransactionManager(rootEntityManagerFactory);
    }

    @PostConstruct
    public void init() {
        runMigrations();
    }

    private void runMigrations() {
        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .schemas("public")
            .locations("classpath:migrations/root")
            .baselineOnMigrate(true)
            .load();

        flyway.migrate();
    }

}
