package com.open.crm.apiControllers;

import com.open.crm.admin.application.UserService;
import com.open.crm.admin.application.exceptions.UserException;
import com.open.crm.admin.application.results.UserResult;
import com.open.crm.admin.entities.user.User;
import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.apiControllers.dto.ApiResponse;
import com.open.crm.apiControllers.dto.ApplicationErrorDto;
import com.open.crm.apiControllers.dto.EmployeeAccess;
import com.open.crm.apiControllers.dto.EmployeeDto;
import com.open.crm.components.mapper.IEmployeeMapper;
import com.open.crm.components.services.SessionService;
import com.open.crm.core.application.errors.EmployeeException;
import com.open.crm.core.application.results.ResultApp;
import com.open.crm.core.application.services.EmployeeService;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import java.util.List;
import java.util.Optional;
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
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public ResponseEntity<ApiResponse> actionCreate(@RequestBody EmployeeDto employee) {
    Author author = sessionEmployeeService.getAuthor();

    ResultApp<Employee> createdEmployee =
        employeeService.createEmployee(employeeMapper.toEntity(employee), author);

    switch (createdEmployee) {
      case ResultApp.Ok<Employee> ok -> {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeMapper.toDto(ok.value()));
      }
      case ResultApp.InvalidData<Employee> invalidData -> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApplicationErrorDto(invalidData.message()));
      }
      default -> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApplicationErrorDto("Unknown error"));
      }
    }
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
  public ResponseEntity<ApiResponse> actionUpdate(
      @PathVariable("id") long id, @RequestBody EmployeeDto data) {
    Author author = sessionEmployeeService.getAuthor();
    Employee employee = employeeMapper.toEntity(data);
    employee.setId(id);
    ResultApp<Employee> result = employeeService.updateEmployeeData(employee, author);

    switch (result) {
      case ResultApp.InvalidData<Employee> invalidData -> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApplicationErrorDto(invalidData.message()));
      }

      case ResultApp.NotFound<Employee> notFound -> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApplicationErrorDto("Employee not found"));
      }

      case ResultApp.IsDeleted<Employee> isDeleted -> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApplicationErrorDto("Cannot update deleted employee"));
      }

      case ResultApp.Ok<Employee> ok -> {
        employee = ok.value();
      }
      default -> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApplicationErrorDto("Unknown error"));
      }
    }

    if (!data.email().equals(employee.getEmail())) {
      result = employeeService.updateEmail(employee, data.email(), author);
      if (result instanceof ResultApp.Ok<Employee> ok) {
        employee = ok.value();
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApplicationErrorDto("Failed to update email"));
      }
    }

    return ResponseEntity.ok(employeeMapper.toDto(employee));
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
  public ResponseEntity<EmployeeAccess> actionGetAccess(@PathVariable("id") long id) {
    Employee employee = employeeService.getEmployeeById(id);

    Optional<User> userOpt = userService.getUserByEmployee(employee);
    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    UserPermission[] permissions = userOpt.get().getPermissions();

    return ResponseEntity.ok(new EmployeeAccess(permissions));
  }

  @PutMapping("{id}/access")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
  public ResponseEntity<EmployeeAccess> putMethodName(
      @PathVariable("id") long id, @RequestBody EmployeeAccess entity) {
    Author author = sessionEmployeeService.getAuthor();

    Employee employee = employeeService.getEmployeeById(id);
    UserResult userResult =
        userService.updateUserPermissionsByEmployee(employee, entity.permissions(), author);

    switch (userResult) {
      case UserResult.Ok ok -> {
        return ResponseEntity.ok(new EmployeeAccess(ok.value().getPermissions()));
      }
      case UserResult.NotFound ignored -> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      default -> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new EmployeeAccess(entity.permissions()));
      }
    }
  }

  @ExceptionHandler({EmployeeException.class, UserException.class})
  public ResponseEntity<ApplicationErrorDto> handleEmployeeException(Exception ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApplicationErrorDto(ex.getMessage()));
  }
}
