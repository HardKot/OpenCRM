package com.open.crm.admin.application;

import com.open.crm.admin.application.events.ApplicationEmailEvent;
import com.open.crm.admin.application.exceptions.UserException;
import com.open.crm.admin.application.interfaces.ISecurityGateway;
import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.application.interfaces.IUserRepository;
import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.admin.entities.user.PasswordType;
import com.open.crm.admin.entities.user.User;
import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.admin.entities.user.UserRole;
import com.open.crm.core.application.IUserService;
import com.open.crm.core.application.errors.NotFoundException;
import com.open.crm.core.application.investigation.events.InviteEmployeeEvent;
import com.open.crm.core.application.investigation.events.UpdateAccessEmployeeEvent;
import com.open.crm.core.application.services.ConflictAccessSetUseCase;
import com.open.crm.core.entities.employee.AccessPermission;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {

  private static final String USER_NOT_FOUND_FOR_EMPLOYEE_ID =
      "User not found for employee with id: ";

  private final IUserRepository userRepository;

  private final ITenantRepository tenantRepository;

  private final PasswordEncoder passwordEncoder;

  private final ApplicationEventPublisher eventPublisher;
  private final ISecurityGateway securityGateway;
  private final ConflictAccessSetUseCase conflictAccessSetUseCase;

  private String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private final Random random = new Random();

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
  }

  @Override
  public void updateUserEmail(Employee employee, String email) throws NotFoundException {
    userRepository
        .findByEmployeeId(employee.getId(), employee.getTenantId())
        .ifPresentOrElse(
            user -> updateUserEmail(user, email),
            () -> new NotFoundException(USER_NOT_FOUND_FOR_EMPLOYEE_ID + employee.getId()));
  }

  @Override
  public void enabledByEmployee(Employee employee) {
    userRepository
        .findByEmployeeId(employee.getId(), employee.getTenantId())
        .ifPresent(
            user -> {
              user.setEnabled(true);
              userRepository.save(user);
            });
  }

  @Override
  public void disabledByEmployee(Employee employee) {
    userRepository
        .findByEmployeeId(employee.getId(), employee.getTenantId())
        .ifPresent(
            user -> {
              user.setEnabled(false);
              userRepository.save(user);
            });
  }

  public User updateUserEmail(User user, String email) throws UserException {
    if (userRepository.existsByEmail(email))
      throw new UserException("Email is already in use: " + email);
    user.setEmail(email);
    User updatedUser = userRepository.save(user);
    securityGateway.refreshAccessUser(updatedUser);
    return updatedUser;
  }

  public User createUserFromEmployee(Employee employee, Author author)
      throws UserException, NotFoundException {
    if (userRepository.existsEmployeeByEmail(employee.getEmail(), employee.getTenantId())) {
      throw new UserException("Employee email is not unique, cannot create user");
    }

    if (employee.isDeleted()) {
      throw new UserException(
          "Cannot create user for deleted employee with id: " + employee.getId());
    }

    Tenant tenant =
        tenantRepository
            .findById(employee.getTenantId())
            .orElseThrow(() -> new NotFoundException("Tenant not found"));
    User data = new User();
    data.setEmail(employee.getEmail());
    data.setEntityName(UserEntity.EMPLOYEE);
    data.setRole(UserRole.ROLE_EMPLOYEE);
    data.setTenant(tenant);
    data.setEntityId(employee.getId());
    createUser(data);

    eventPublisher.publishEvent(new InviteEmployeeEvent(employee, author));

    return data;
  }

  public User createOwnerUser(Tenant tenant, String email, long entityId) {
    User data = new User();
    data.setEmail(email);
    data.setEntityName(UserEntity.EMPLOYEE);
    data.setRole(UserRole.ROLE_OWNER);
    data.setTenant(tenant);
    data.setEntityId(entityId);
    return createUser(data);
  }

  public Optional<User> getUserByEmployee(Employee employee) {

    return userRepository.findByEmployeeId(employee.getId(), employee.getTenantId());
  }

  private User createUser(@Validated User data) {
    if (Objects.isNull(data.getEmail()) || data.getEmail().isBlank())
      throw new UserException("Email cannot be empty");
    if (userRepository.existsByEmail(data.getEmail()))
      throw new UserException("Email is already in use");

    String password = generatePassword();
    data.setPassword(passwordEncoder.encode(password));
    userRepository.save(data);

    eventPublisher.publishEvent(
        new ApplicationEmailEvent(
            this,
            data.getEmail(),
            "Welcome!",
            "email/welcome-email",
            Map.of("username", data.getUsername(), "password", password)));

    return data;
  }

  public User updateUserPermission(Employee employee, AccessPermission[] permissions) {
    return getUserByEmployee(employee)
        .<User>map(user -> updateUserPermissions(user, permissions))
        .orElseThrow(
            () -> new NotFoundException(USER_NOT_FOUND_FOR_EMPLOYEE_ID + employee.getId()));
  }

  public User updateUserPermissions(User user, AccessPermission[] permissions) {
    if (user.getRole().equals(UserRole.ROLE_OWNER)) {
      throw new UserException("Cannot change permissions for owner");
    }

    if (user.getRole().equals(UserRole.ROLE_ADMIN)) {
      throw new UserException("Cannot change permissions for admin");
    }
    Set<AccessPermission> permissionSet = Set.of(permissions);

    ConflictAccessSetUseCase.Result conflictResult =
        conflictAccessSetUseCase.execute(permissionSet);
    if (!conflictResult.isValid()) {
      throw new UserException(conflictResult.message());
    }

    user.setPermissions(permissionSet);

    securityGateway.refreshAccessUser(user);

    user = userRepository.save(user);

    return user;
  }

  public User updateUserPermissionsByEmployee(
      Employee employee, AccessPermission[] permissions, Author author) {

    User result =
        getUserByEmployee(employee)
            .<User>map(user -> updateUserPermissions(user, permissions))
            .orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_FOR_EMPLOYEE_ID + employee.getId()));

    eventPublisher.publishEvent(new UpdateAccessEmployeeEvent(employee, author));

    return result;
  }

  public User updateUserPermissions(UUID userId, AccessPermission[] permissions) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

    return updateUserPermissions(user, permissions);
  }

  public User updatePassword(User user, String password) {
    if (matchPassword(password, user)) {
      throw new UserException("New password cannot be the same as the old password");
    }

    PasswordType passwordType = getPasswordType(password);
    if (passwordType == PasswordType.WEAK || passwordType == PasswordType.SIMPLE) {
      throw new UserException("Password is too weak");
    }
    user.setPassword(passwordEncoder.encode(password));
    return userRepository.save(user);
  }

  public String generatePassword() {
    StringBuilder password = new StringBuilder();
    for (int i = 0; i < 10; i++) {
      password.append(chars.charAt(random.nextInt(chars.length())));
    }

    return password.toString();
  }

  public User recreatePassword(User user) {
    String password = generatePassword();
    user.setPassword(passwordEncoder.encode(password));
    User updatedUser = userRepository.save(user);

    eventPublisher.publishEvent(
        new ApplicationEmailEvent(
            this,
            user.getEmail(),
            "Password Reset",
            "email/reset-password-email",
            Map.of("username", user.getUsername(), "password", password)));

    return updatedUser;
  }

  public PasswordType getPasswordType(String password) {
    int score = 0;

    if (password.length() >= 6) score++;
    if (password.length() >= 10) score++;
    if (password.matches("(?=.*\\d).*")) score++;
    if (password.matches("(?=.*[a-z]).*")) score++;
    if (password.matches("(?=.*[A-Z]).*")) score++;

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

  public boolean matchPassword(String password, User user) {
    return passwordEncoder.matches(password, user.getPassword());
  }
}
