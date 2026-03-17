package com.open.crm.controllers;

import com.open.crm.admin.application.UserService;
import com.open.crm.admin.application.exceptions.UserException;
import com.open.crm.admin.entities.user.User;
import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.components.mapper.IEmployeeMapper;
import com.open.crm.components.services.SessionService;
import com.open.crm.controllers.dto.ApplicationErrorDto;
import com.open.crm.controllers.dto.EmployeeAccess;
import com.open.crm.controllers.dto.EmployeeDto;
import com.open.crm.core.application.errors.EmployeeException;
import com.open.crm.core.application.services.EmployeeService;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;

  private final UserService userService;

  private final SessionService sessionEmployeeService;
  private final IEmployeeMapper employeeMapper;

  @PostMapping
  @Transactional
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public EmployeeDto actionCreate(@RequestBody EmployeeDto employee) {
    Author author = sessionEmployeeService.getAuthor();

    Employee createdEmployee =
        employeeService.createEmployee(employeeMapper.toEntity(employee), author);

    return employeeMapper.toDto(createdEmployee);
  }

  @GetMapping
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
  public List<EmployeeDto> actionGetAll(
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "100") int size) {
    return employeeService
        .getEmployeeSelector()
        .getItems(page - 1, size, sessionEmployeeService.isShowDeleted())
        .stream()
        .map(employeeMapper::toDto)
        .toList();
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public EmployeeDto actionUpdate(@PathVariable("id") long id, @RequestBody EmployeeDto data) {
    Author author = sessionEmployeeService.getAuthor();
    Employee employee = employeeMapper.toEntity(data);
    employee.setId(id);
    employee = employeeService.updateEmployeeData(employee, author);

    if (!data.email().equals(employee.getEmail())) {
      employee = employeeService.updateEmail(employee, data.email(), author);
    }

    return employeeMapper.toDto(employee);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
  public EmployeeDto actionGet(@PathVariable("id") long id) {
    return employeeMapper.toDto(employeeService.getEmployeeById(id));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public EmployeeDto actionDelete(@PathVariable("id") long id) {
    Author author = sessionEmployeeService.getAuthor();
    Employee employee = employeeService.getEmployeeById(id);
    employee = employeeService.deleteEmployee(employee, author);
    return employeeMapper.toDto(employee);
  }

  @PostMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public EmployeeDto actionRestore(@PathVariable("id") long id) {
    Author author = sessionEmployeeService.getAuthor();
    Employee employee = employeeService.getEmployeeById(id);
    employee = employeeService.restoreEmployee(employee, author);
    return employeeMapper.toDto(employee);
  }

  @PostMapping("/{id}/invite")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
  public EmployeeDto actionInvite(@PathVariable("id") long id) {
    Author author = sessionEmployeeService.getAuthor();

    Employee employee = employeeService.getEmployeeById(id);
    userService.createUserFromEmployee(employee, author);

    return employeeMapper.toDto(employee);
  }

  @GetMapping("{id}/access")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
  public EmployeeAccess actionGetAccess(@PathVariable("id") long id) {
    Employee employee = employeeService.getEmployeeById(id);
    User user = userService.getUserByEmployee(employee);

    UserPermission[] permissions = user.getPermissions();

    return new EmployeeAccess(permissions);
  }

  @PutMapping("{id}/access")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
  public EmployeeAccess putMethodName(
      @PathVariable("id") long id, @RequestBody EmployeeAccess entity) {
    Author author = sessionEmployeeService.getAuthor();

    Employee employee = employeeService.getEmployeeById(id);
    User user = userService.updateUserPermissionsByEmployee(employee, entity.permissions(), author);

    return new EmployeeAccess(user.getPermissions());
  }

  @ExceptionHandler({EmployeeException.class, UserException.class})
  public ResponseEntity<ApplicationErrorDto> handleEmployeeException(Exception ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApplicationErrorDto(ex.getMessage()));
  }
}
