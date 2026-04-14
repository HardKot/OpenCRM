package com.open.crm.config;

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {"com.open.crm.admin"},
    entityManagerFactoryRef = "adminEntityManagerFactory",
    transactionManagerRef = "adminTransactionManager")
@RequiredArgsConstructor
public class AdminEntityManagerConfig {

  private final DataSource dataSource;

  @Bean(name = "adminEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean adminEntityManagerFactory() {
    LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    emf.setDataSource(dataSource);
    emf.setPackagesToScan("com.open.crm.admin");
    emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    emf.setPersistenceUnitName("adminPU");

    Map<String, Object> props = new HashMap<>();
    props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    props.put("hibernate.default_schema", "public");
    props.put("hibernate.show_sql", true);
    props.put("hibernate.hbm2ddl.auto", "none");

    emf.setJpaPropertyMap(props);
    return emf;
  }

  @Bean(name = "adminTransactionManager")
  public PlatformTransactionManager adminTransactionManager(
      @Qualifier("adminEntityManagerFactory") EntityManagerFactory adminEntityManagerFactory) {
    JpaTransactionManager tm = new JpaTransactionManager(adminEntityManagerFactory);
    tm.setDataSource(null);
    return tm;
  }
}
