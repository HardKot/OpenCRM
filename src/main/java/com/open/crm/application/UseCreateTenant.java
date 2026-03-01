package com.open.crm.application;

import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.open.crm.application.common.Result;
import com.open.crm.application.errors.ApplicationException;
import com.open.crm.domain.common.Tenant;
import com.open.crm.domain.common.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UseCreateTenant {
    private final Gateway gateway;

    public static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*$");

    public record CreateTenantResult(
        User user,
        Tenant tenant
    ) {}

    
    public interface Gateway {
        boolean existByEmail(String email);
        String generatePassword();
        String hashPassword(String password);
        void saveTenant(Tenant tenant);
        void saveUser(User user);
        boolean sendEmail(String email, String theme, String text);
    }

    public Result<CreateTenantResult, ApplicationException> execute(String email) {
        if (Objects.isNull(email) || email.isBlank()) return Result.failure(new ApplicationException("Email cannot be empty"));
        if (email.length() > 255) return Result.failure(new ApplicationException("Email cannot be longer than 255 characters"));
        if (!EMAIL_REGEX.matcher(email).matches()) return Result.failure(new ApplicationException("Email is not valid"));
        if (gateway.existByEmail(email)) return Result.failure(new ApplicationException("Email is already taken"));

        Tenant tenant = createTenant();
        String password = gateway.generatePassword();
        User user = createUser(tenant, email, password);

        gateway.sendEmail(email, "Welcome to OpenCRM", "Your account has been created successfully. Your password is: " + password + ". Please change it after your first login.");


        return Result.success(new CreateTenantResult(user, tenant));
    }

    private Tenant createTenant() {
        Tenant tenant = new Tenant();
        gateway.saveTenant(tenant);
        return tenant;
    }

    private User createUser(Tenant tenant, String email, String password) {
        User user = new User();
        user.setTenant(tenant);
        user.setEmail(email);
        user.setPassword(gateway.hashPassword(password));

        gateway.saveUser(user);

        return user;
    }
}
