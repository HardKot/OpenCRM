package com.open.crm.admin.application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.open.crm.admin.application.events.ApplicationEmailEvent;
import com.open.crm.admin.application.exceptions.UserException;
import com.open.crm.admin.application.interfaces.ISecurityGateway;
import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.application.interfaces.IUserRepository;
import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.admin.entities.user.PasswordType;
import com.open.crm.admin.entities.user.User;
import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.admin.entities.user.UserRole;
import com.open.crm.core.application.IUserService;
import com.open.crm.core.application.InvestigationLogCreator;
import com.open.crm.core.application.errors.NotFoundException;
import com.open.crm.core.application.repositories.IInvestigationLogRepository;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.core.entities.investigationLog.InvestigationLog;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {

    private final IUserRepository userRepository;

    private final ITenantRepository tenantRepository;

    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher eventPublisher;

    private final InvestigationLogCreator investigationLogCreator;
    private final IInvestigationLogRepository investigationLogRepository;
    private final ISecurityGateway securityGateway;

    private String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private Pattern EMAIL_REGEX = Pattern
            .compile("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*$");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Override
    public void updateUserEmail(Employee employee, String email) {
        userRepository.findByEmployeeId(employee.getId(), employee.getTenantId()).ifPresent(user -> {
            updateUserEmail(user, email);
        });
    }

    @Override
    public void enabledByEmployee(Employee employee) {
        userRepository.findByEmployeeId(employee.getId(), employee.getTenantId()).ifPresent(user -> {
            user.setEnabled(true);
            userRepository.save(user);
        });
    }

    @Override
    public void disabledByEmployee(Employee employee) {
        userRepository.findByEmployeeId(employee.getId(), employee.getTenantId()).ifPresent(user -> {
            user.setEnabled(false);
            userRepository.save(user);
        });
    }

    public User updateUserEmail(User user, String email) throws UserException {
        if (userRepository.existsByEmail(email))
            throw new UserException("Email is already taken");
        user.setEmail(email);
        User updatedUser = userRepository.save(user);
        securityGateway.refreshAccessUser(updatedUser);
        return updatedUser;
    }

    public User createUserFromEmployee(Employee employee, Author author) throws UserException, NotFoundException {
        if (userRepository.existsEmployeeByEmail(employee.getEmail(), employee.getTenantId())) {
            throw new UserException("User already exists for employee with id: " + employee.getId());
        }

        if (employee.isDeleted()) {
            throw new UserException("Cannot create user for deleted employee with id: " + employee.getId());
        }

        Tenant tenant = tenantRepository.findById(employee.getTenantId())
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        User data = new User();
        data.setEmail(employee.getEmail());
        data.setEntityName(UserEntity.EMPLOYEE);
        data.setRole(UserRole.ROLE_EMPLOYEE);
        data.setTenant(tenant);
        data.setEntityId(employee.getId());
        createUser(data);
        InvestigationLog log = investigationLogCreator.inviteEmployeeLog(employee, author);
        investigationLogRepository.save(log);

        return data;
    }

    public User createOwnerUser(Tenant tenant, String email, long entityId) throws UserException {
        User data = new User();
        data.setEmail(email);
        data.setEntityName(UserEntity.EMPLOYEE);
        data.setRole(UserRole.ROLE_OWNER);
        data.setTenant(tenant);
        data.setEntityId(entityId);
        return createUser(data);
    }

    public User getUserByEmployee(Employee employee) throws UserException {
        return userRepository.findByEmployeeId(employee.getId(), employee.getTenantId())
                .orElseThrow(() -> new NotFoundException("User not found for employee with id: " + employee.getId()));
    }

    private User createUser(User data) throws UserException {
        if (Objects.isNull(data.getEmail()) || data.getEmail().isBlank())
            throw new UserException("Email cannot be empty");
        if (data.getEmail().length() > 255)
            throw new UserException("Email cannot be longer than 255 characters");
        if (!EMAIL_REGEX.matcher(data.getEmail()).matches())
            throw new UserException("Email is not valid");
        if (userRepository.existsByEmail(data.getEmail()))
            throw new UserException("Email is already taken");

        String password = generatePassword();
        data.setPassword(passwordEncoder.encode(password));
        userRepository.save(data);

        eventPublisher.publishEvent(new ApplicationEmailEvent(this, data.getEmail(), "Welcome!", "email/welcome-email",
                Map.of("username", data.getUsername(), "password", password)));

        return data;
    }

    public User updateUserPermission(Employee employee, UserPermission[] permissions) throws UserException {
        User user = getUserByEmployee(employee);
        return updateUserPermissions(user, permissions);
    }

    public User updateUserPermissions(User user, UserPermission[] permissions) {
        if (user.getRole().equals(UserRole.ROLE_OWNER)) {
            throw new UserException("Cannot change permissions for owner");
        }

        if (user.getRole().equals(UserRole.ROLE_ADMIN)) {
            throw new UserException("Cannot change permissions for admin");
        }
        user.setPermissions(new HashSet<>(Arrays.asList(permissions)));

        securityGateway.refreshAccessUser(user);

        return userRepository.save(user);
    }

    public User updateUserPermissionsByEmployee(Employee employee, UserPermission[] permissions, Author author)
            throws UserException {
        User user = getUserByEmployee(employee);
        User updatedUser = updateUserPermissions(user, permissions);

        InvestigationLog log = investigationLogCreator.updateAccessEmployeeLog(employee, author);
        investigationLogRepository.save(log);

        return updatedUser;
    }

    public User updateUserPermissions(UUID userId, UserPermission[] permissions, Author author) throws UserException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        user = updateUserPermissions(user, permissions);

        return user;
    }

    public User updatePassword(User user, String password) throws UserException {
        PasswordType passwordType = getPasswordType(password);
        if (passwordType == PasswordType.WEAK) {
            throw new UserException("Password is too weak");
        }
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public String generatePassword() {
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();

    }

    public PasswordType getPasswordType(String password) {
        int score = 0;

        if (password.length() >= 6)
            score++;
        if (password.length() >= 10)
            score++;
        if (password.matches("(?=.*[0-9]).*"))
            score++;
        if (password.matches("(?=.*[a-z]).*"))
            score++;
        if (password.matches("(?=.*[A-Z]).*"))
            score++;

        if (score >= 5) {
            return PasswordType.HARD;
        } else if (score >= 3) {
            return PasswordType.MEDIUM;
        } else if (score >= 1) {
            return PasswordType.SIMPLE;
        } else {
            return PasswordType.WEAK;
        }
    }

    public boolean matchPassword(String password, String hash) {
        return passwordEncoder.matches(password, hash);
    }

}
