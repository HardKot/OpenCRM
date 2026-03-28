package com.open.crm.admin.application;

import com.open.crm.admin.application.events.ApplicationEmailEvent;
import com.open.crm.admin.application.exceptions.UserException;
import com.open.crm.admin.application.interfaces.ISecurityGateway;
import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.application.interfaces.IUserRepository;
import com.open.crm.admin.application.results.UserResult;
import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.admin.entities.user.PasswordType;
import com.open.crm.admin.entities.user.User;
import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.admin.entities.user.UserRole;
import com.open.crm.core.application.IUserService;
import com.open.crm.core.application.errors.NotFoundException;
import com.open.crm.core.application.investigation.events.InviteEmployeeEvent;
import com.open.crm.core.application.investigation.events.UpdateAccessEmployeeEvent;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {

  private final IUserRepository userRepository;

  private final ITenantRepository tenantRepository;

  private final PasswordEncoder passwordEncoder;

  private final ApplicationEventPublisher eventPublisher;
  private final ISecurityGateway securityGateway;

  private String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  private Pattern EMAIL_REGEX =
      Pattern.compile("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*$");

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
  }

  @Override
  public void updateUserEmail(Employee employee, String email) {
    userRepository
        .findByEmployeeId(employee.getId(), employee.getTenantId())
        .map(user -> updateUserEmail(user, email))
        .ifPresentOrElse(
            result -> {
              switch (result) {
                case UserResult.InvalidData(String message) -> throw new UserException(message);
                case UserResult.NoUniqueEmail() ->
                    throw new UserException("Email is already in use: " + email);
                case UserResult.IsDeleted() ->
                    throw new UserException(
                        "Cannot update email for deleted employee with id: " + employee.getId());
                case UserResult.NotFound() ->
                    throw new NotFoundException(
                        "User not found for employee with id: " + employee.getId());
                case UserResult.Ok(User value) -> {}
              }
            },
            () -> {
              throw new NotFoundException(
                  "User not found for employee with id: " + employee.getId());
            });
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

  public UserResult updateUserEmail(User user, String email) {
    if (userRepository.existsByEmail(email)) return new UserResult.NoUniqueEmail();
    user.setEmail(email);
    User updatedUser = userRepository.save(user);
    securityGateway.refreshAccessUser(updatedUser);
    return new UserResult.Ok(updatedUser);
  }

  public UserResult createUserFromEmployee(Employee employee, Author author)
      throws UserException, NotFoundException {
    if (userRepository.existsEmployeeByEmail(employee.getEmail(), employee.getTenantId())) {
      return new UserResult.NoUniqueEmail();
    }

    if (employee.isDeleted()) {
      return new UserResult.InvalidData(
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

    return new UserResult.Ok(data);
  }

  public UserResult createOwnerUser(Tenant tenant, String email, long entityId) {
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

  private UserResult createUser(User data) {
    if (Objects.isNull(data.getEmail()) || data.getEmail().isBlank())
      return new UserResult.InvalidData("Email cannot be empty");
    if (data.getEmail().length() > 255)
      return new UserResult.InvalidData("Email cannot be longer than 255 characters");
    if (!EMAIL_REGEX.matcher(data.getEmail()).matches())
      return new UserResult.InvalidData("Email is not valid");
    if (userRepository.existsByEmail(data.getEmail())) return new UserResult.NoUniqueEmail();

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

    return new UserResult.Ok(data);
  }

  public UserResult updateUserPermission(Employee employee, UserPermission[] permissions) {
    return getUserByEmployee(employee)
        .<UserResult>map(user -> updateUserPermissions(user, permissions))
        .orElseGet(UserResult.NotFound::new);
  }

  public UserResult updateUserPermissions(User user, UserPermission[] permissions) {
    if (user.getRole().equals(UserRole.ROLE_OWNER)) {
      return new UserResult.InvalidData("Cannot change permissions for owner");
    }

    if (user.getRole().equals(UserRole.ROLE_ADMIN)) {
      return new UserResult.InvalidData("Cannot change permissions for admin");
    }
    user.setPermissions(new HashSet<>(Arrays.asList(permissions)));

    securityGateway.refreshAccessUser(user);

    user = userRepository.save(user);

    return new UserResult.Ok(user);
  }

  public UserResult updateUserPermissionsByEmployee(
      Employee employee, UserPermission[] permissions, Author author) {

    UserResult result =
        getUserByEmployee(employee)
            .<UserResult>map(user -> updateUserPermissions(user, permissions))
            .orElseGet(UserResult.NotFound::new);

    if (result instanceof UserResult.Ok) {
      eventPublisher.publishEvent(new UpdateAccessEmployeeEvent(employee, author));
    }

    return result;
  }

  public UserResult updateUserPermissions(
      UUID userId, UserPermission[] permissions, Author author) {
    UserResult result =
        userRepository
            .findById(userId)
            .<UserResult>map(UserResult.Ok::new)
            .orElse(new UserResult.NotFound());

    if (result instanceof UserResult.Ok) {
      result = updateUserPermissions(((UserResult.Ok) result).value(), permissions);
    } else {
      return result;
    }

    return result;
  }

  public UserResult updatePassword(User user, String password) {
    if (matchPassword(password, user)) {
      return new UserResult.InvalidData("New password cannot be the same as the old password");
    }

    PasswordType passwordType = getPasswordType(password);
    if (passwordType == PasswordType.WEAK) {
      throw new UserException("Password is too weak");
    }
    user.setPassword(passwordEncoder.encode(password));
    return new UserResult.Ok(userRepository.save(user));
  }

  public String generatePassword() {
    StringBuilder password = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < 10; i++) {
      password.append(chars.charAt(random.nextInt(chars.length())));
    }

    return password.toString();
  }

  public UserResult recreatePassword(User user) {
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

    return new UserResult.Ok(updatedUser);
  }

  public PasswordType getPasswordType(String password) {
    int score = 0;

    if (password.length() >= 6) score++;
    if (password.length() >= 10) score++;
    if (password.matches("(?=.*[0-9]).*")) score++;
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
