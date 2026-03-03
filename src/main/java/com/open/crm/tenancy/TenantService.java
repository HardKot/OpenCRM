package com.open.crm.tenancy;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.open.crm.components.events.ApplicationEmailEvent;
import com.open.crm.components.events.ApplicationSchemaEvent;
import com.open.crm.core.application.EmployeeService;
import com.open.crm.core.domain.employee.Employee;
import com.open.crm.security.IUserRepository;
import com.open.crm.security.PasswordService;
import com.open.crm.security.User;
import com.open.crm.security.UserService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TenantService {
    private final ITenantRepository tenantRepository;
    private final UserService userService;
    private final IUserRepository userRepository;
    private final PasswordService passwordService;  
    private final ApplicationEventPublisher eventPublisher;
    private final JdbcTemplate jdbc;
    private final DataSource dataSource;
    private final EmployeeService employeeService;


    public static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*$");


    public Tenant createTenant(String email) throws TenantException {
        if (Objects.isNull(email) || email.isBlank()) throw new TenantException("Email cannot be empty");
        if (email.length() > 255) throw new TenantException("Email cannot be longer than 255 characters");
        if (!EMAIL_REGEX.matcher(email).matches()) throw new TenantException("Email is not valid");
        if (userRepository.existsByEmail(email)) throw new TenantException("Email is already taken");

        String password = passwordService.generatePassword();

        Tenant tenant = initTenant();


        tenantRepository.save(tenant);
        User user = userService.createUser(email, tenant, 1L);
        userService.updatePassword(user, password);

        ApplicationEmailEvent emailEvent = new ApplicationEmailEvent(
            this, 
            user.getEmail(), 
            "Welcome to OpenCRM", 
            "email/welcome-email",
            Map.of("email", user.getEmail(), "password", password)
        );

        ApplicationSchemaEvent schemaEvent = new ApplicationSchemaEvent(
            this, 
            tenant.getSchemaName(),
            tenant.getId(),
            user
        );

        eventPublisher.publishEvent(emailEvent);
        eventPublisher.publishEvent(schemaEvent);



        return tenant;
    }

    private Tenant initTenant() {
        Tenant tenant = new Tenant();
        tenant.setActive(true);
        tenant.setReady(false);
        return tenant;
    }


    @Async
    @EventListener
    public void createSchemaForTenant(ApplicationSchemaEvent event) {
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (Statement st = con.createStatement()) {
            jdbc.execute("CREATE SCHEMA IF NOT EXISTS " + event.getSchemaName());
            
            jdbc.update("UPDATE tenants SET active = true, is_ready = true WHERE id = ?", event.getTenantId());

            runMigrationTenant(event.getSchemaName());

            st.execute("SET search_path TO " + event.getSchemaName() + ", public");
            TenantContext.setCurrentTenant(event.getTenantId());
            createRoot(event);
        } catch (Exception e) {
            throw new RuntimeException("Error creating tenant schema: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
            TenantContext.clear();
        }
            
    }

    private Employee createRoot(ApplicationSchemaEvent event) {
        Employee root = new Employee();
        root.setFirstname("Root");
        root.setLastname("User");
        root.setEmail(event.getUser().getEmail());
        root.setTenantId(event.getTenantId());

        employeeService.createEmployee(root);
        return root;
    }

    public void runMigrationTenant(String schemaName) {
        Flyway flyway = Flyway.configure()
            .dataSource(dataSource)
            .schemas(schemaName)
            .locations("classpath:migrations/tenant")
            .baselineOnMigrate(true)
            .validateOnMigrate(true)
            .load();

        flyway.migrate();
    }


    @PostConstruct
    public void init() {
        List<Tenant> tenants = tenantRepository.findByReady(true);
       
        for (Tenant tenant : tenants) {
            try {
                log.info("Running migration for tenant {} with schema {}", tenant.getId(), tenant.getSchemaName());
                runMigrationTenant(tenant.getSchemaName());
                log.info("Migration completed for tenant {}", tenant.getId());
            } catch (Exception e) {
                log.error("Error running migration for tenant {}: {}", tenant.getId(), e.getMessage());
            }
        }

    }
}
