package com.open.crm.components.services;

import com.open.crm.admin.application.UserService;
import com.open.crm.admin.entities.user.User;
import com.open.crm.components.mapper.IEmployeeMapper;
import com.open.crm.core.application.services.EmployeeService;
import com.open.crm.core.entities.employee.AccessPermission;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.dto.employee.EmployeeUserDto;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveEmployeeUserUseCase {
  private final EmployeeService employeeService;
  private final UserService userService;

  private final IEmployeeMapper employeeMapper;

  @Transactional
  public EmployeeUserDto execute(EmployeeUserDto form, Author author) {
    Employee employeeEntity = employeeMapper.toEntity(form.employee());

    Employee employee = saveEmployee(employeeEntity, author);

    if (Objects.isNull(form.isAccessAllowed())) {
      return employeeMapper.toFormDto(employee, null);
    }

    updateEnabledEmployeeUser(employeeEntity, form.isAccessAllowed(), author);

    User user = updateEmployeeUserPermissions(employeeEntity, form.permissions(), author);

    return employeeMapper.toFormDto(employeeEntity, user);
  }

  private Employee saveEmployee(Employee employee, Author author) {
    if (Objects.isNull(employee.getId()) || employee.getId() == 0) {
      return employeeService.createEmployee(employee, author);
    }
    return employeeService.updateEmployeeData(employee, author);
  }

  private User updateEnabledEmployeeUser(Employee employee, boolean hasAccess, Author author) {
    if (hasAccess) {
      return enabledEmployeeUser(employee, author);
    }
    return disabledEmployeeUser(employee, author);
  }

  private User enabledEmployeeUser(Employee employee, Author author) {
    Optional<User> userOpt = userService.getUserByEmployee(employee);

    if (userOpt.isPresent()) {
      userService.enabledByEmployee(employee);
      return userOpt.get();
    }

    return userService.createUserFromEmployee(employee, author);
  }

  private User disabledEmployeeUser(Employee employee, Author author) {
    Optional<User> userOpt = userService.getUserByEmployee(employee);

    if (userOpt.isEmpty()) return null;

    userService.disabledByEmployee(employee);
    return userOpt.orElse(null);
  }

  private User updateEmployeeUserPermissions(
      Employee employee, AccessPermission[] permissions, Author author) {
    return userService.updateUserPermission(employee, permissions);
  }
}
