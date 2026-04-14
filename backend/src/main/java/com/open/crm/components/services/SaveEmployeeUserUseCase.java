package com.open.crm.components.services;

import com.open.crm.admin.application.UserService;
import com.open.crm.admin.application.results.UserResult;
import com.open.crm.admin.entities.user.User;
import com.open.crm.components.mapper.IEmployeeMapper;
import com.open.crm.core.application.results.ResultApp;
import com.open.crm.core.application.services.EmployeeService;
import com.open.crm.core.entities.employee.AccessPermission;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.dto.EmployeeUserDto;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
public class SaveEmployeeUserUseCase {
  private final EmployeeService employeeService;
  private final UserService userService;

  private final IEmployeeMapper employeeMapper;

  @Transactional
  public ResultApp<EmployeeUserDto> execute(EmployeeUserDto form, Author author) {
    Employee employeeEntity = employeeMapper.toEntity(form.employee());
    User user = null;

    ResultApp<Employee> resultEmployee = saveEmployee(employeeEntity, author);

    if (resultEmployee instanceof ResultApp.Ok<Employee> okEmployeeResult) {
      employeeEntity = okEmployeeResult.value();
    } else {
      return mapFromEmployeeResult(resultEmployee);
    }

    if (Objects.isNull(form.isAccessAllowed())) {
      EmployeeUserDto formDto = employeeMapper.toFormDto(employeeEntity, null);
      return new ResultApp.Ok<>(formDto);
    }

    ResultApp<User> resultUser =
        updateEnabledEmployeeUser(employeeEntity, form.isAccessAllowed(), author);

    if (resultUser instanceof ResultApp.Ok<User> okUserVisibleResult) {
      user = okUserVisibleResult.value();
    } else {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return switch (resultUser) {
        case ResultApp.NotFound<User> notFound -> new ResultApp.NotFound<>();
        case ResultApp.InvalidData<User> invalidData ->
            new ResultApp.InvalidData<>(invalidData.message());
        default -> new ResultApp.InvalidData<>("Unknown error");
      };
    }

    resultUser = updateEmployeeUserPermissions(employeeEntity, form.permissions(), author);

    if (resultUser instanceof ResultApp.Ok<User> okUserResult) {
      user = okUserResult.value();
    } else {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return switch (resultUser) {
        case ResultApp.NotFound<User> notFound -> new ResultApp.NotFound<>();
        case ResultApp.InvalidData<User> invalidData ->
            new ResultApp.InvalidData<>(invalidData.message());
        default -> new ResultApp.InvalidData<>("Unknown error");
      };
    }

    EmployeeUserDto formDto = employeeMapper.toFormDto(employeeEntity, user);
    return new ResultApp.Ok<>(formDto);
  }

  private ResultApp<Employee> saveEmployee(Employee employee, Author author) {
    ResultApp<Employee> result;
    if (Objects.isNull(employee.getId()) || employee.getId() == 0) {
      result = employeeService.createEmployee(employee, author);
    } else {
      result = employeeService.updateEmployeeData(employee, author);
    }
    return result;
  }

  private ResultApp<User> updateEnabledEmployeeUser(
      Employee employee, boolean hasAccess, Author author) {
    if (hasAccess) {
      return enabledEmployeeUser(employee, author);
    }
    return disabledEmployeeUser(employee, author);
  }

  private ResultApp<User> enabledEmployeeUser(Employee employee, Author author) {
    Optional<User> userOpt = userService.getUserByEmployee(employee);

    if (userOpt.isPresent()) {
      userService.enabledByEmployee(employee);
      return new ResultApp.Ok<User>(userOpt.get());
    }

    UserResult userResult = userService.createUserFromEmployee(employee, author);
    return switch (userResult) {
      case UserResult.Ok okResult -> new ResultApp.Ok<User>(okResult.value());
      case UserResult.NotFound notFound -> new ResultApp.NotFound<>();
      case UserResult.InvalidData invalidData -> new ResultApp.InvalidData<>(invalidData.message());
      case UserResult.IsDeleted isDeleted ->
          new ResultApp.InvalidData<>("Employee is deleted, cannot create user");
      case UserResult.NoUniqueEmail noUniqueEmail ->
          new ResultApp.InvalidData<>("Employee email is not unique, cannot create user");
    };
  }

  private ResultApp<User> disabledEmployeeUser(Employee employee, Author author) {
    Optional<User> userOpt = userService.getUserByEmployee(employee);

    if (userOpt.isEmpty()) {
      return new ResultApp.Ok<>(null);
    }

    userService.disabledByEmployee(employee);
    return new ResultApp.Ok<>(userOpt.orElse(null));
  }

  private ResultApp<User> updateEmployeeUserPermissions(
      Employee employee, AccessPermission[] permissions, Author author) {
    UserResult result = userService.updateUserPermission(employee, permissions);
    return switch (result) {
      case UserResult.Ok okResult -> new ResultApp.Ok<User>(okResult.value());
      case UserResult.NotFound notFound -> new ResultApp.NotFound<>();
      case UserResult.InvalidData invalidData -> new ResultApp.InvalidData<>(invalidData.message());
      case UserResult.IsDeleted isDeleted ->
          new ResultApp.InvalidData<>("Employee is deleted, cannot update user permissions");
      case UserResult.NoUniqueEmail noUniqueEmail ->
          new ResultApp.InvalidData<>(
              "Employee email is not unique, cannot update user permissions");
    };
  }

  private ResultApp<EmployeeUserDto> mapFromEmployeeResult(ResultApp<Employee> result) {
    return switch (result) {
      case ResultApp.Ok<Employee> okResult ->
          new ResultApp.Ok<>(employeeMapper.toFormDto(okResult.value(), null));
      case ResultApp.NotFound<Employee> notFound -> new ResultApp.NotFound<>();
      case ResultApp.InvalidData<Employee> invalidDataResult ->
          new ResultApp.InvalidData<>(invalidDataResult.message());
      default -> new ResultApp.InvalidData<>("Unknown error");
    };
  }
}
