package com.open.crm.admin.application;

import org.springframework.stereotype.Service;

import com.open.crm.admin.application.exceptions.TenantException;
import com.open.crm.admin.application.interfaces.IDatabase;
import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.application.interfaces.IUserRepository;
import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.admin.entities.user.User;
import com.open.crm.admin.entities.user.UserRole;
import com.open.crm.core.application.repositories.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UseCreateTenant {

    private final TenantService tenantService;

    private final ITenantRepository tenantRepository;

    private final UserService userService;

    private final IDatabase database;

    private final IUserRepository userRepository;

    private final IEmployeeRepository employeeRepository;

    public record Params(String email) {
    }

    public Tenant execute(Params params) throws TenantException {
        if (userRepository.existsByEmail(params.email()))
            throw new TenantException("Email is already taken");

        try {

            Tenant tenant = tenantService.generateTenant();
            User user = createUserForTenant(tenant, params.email());
            user = userService.createUser(user, params.email());

            database.copySchema(database.getTemplateTenantSchemaName(), tenant.getSchemaName());
            database.schemaChangeTenant(tenant.getSchemaName(), tenant);
            database.setContextTenant(tenant);

            employeeRepository.findById(1L).ifPresent(employee -> {
                employee.setEmail(params.email());
                employeeRepository.save(employee);
            });

            tenant.setReady(true);
            tenantRepository.save(tenant);

            return tenant;
        }
        catch (Exception e) {
            log.error("Error creating tenant", e);
            throw new TenantException("Error creating tenant: " + e.getMessage());
        }
    }

    private User createUserForTenant(Tenant tenant, String email) {
        User user = new User();
        user.setEmail(email);
        user.setTenant(tenant);
        user.setEntityId(1);
        user.setRole(UserRole.ROLE_OWNER);
        return user;
    }

}
