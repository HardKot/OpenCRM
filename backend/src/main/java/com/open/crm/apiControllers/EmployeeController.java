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
import com.open.crm.apiControllers.dto.PageResponse;
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
  public PageResponse.EmployeePageDto actionGetAll(
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "100") int size,
      @RequestParam(name = "fullname", required = false) String fullname,
      @RequestParam(name = "position", required = false) String position,
      @RequestParam(name = "isDeleted", required = false) boolean isDeleted,
      @RequestParam(name = "sortBy", required = false) String sortBy,
      @RequestParam(name = "sortDirection", required = false) String sortDirection
    ) {

    boolean showDeleted = isDeleted && sessionEmployeeService.isShowDeleted();

    List<EmployeeDto> list =
        employeeService
            .getEmployeeSelector()
            .getItems(page - 1, size, showDeleted, sortBy, sortDirection)
            .stream()
            .map(employeeMapper::toDto)
            .toList();

    long totalElements =
        employeeService.getEmployeeSelector().countItems(showDeleted);

    return new PageResponse.EmployeePageDto(
        totalElements,
        (int) Math.ceil((double) totalElements / size),
        list.toArray(new EmployeeDto[0]));
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
  public ResponseEntity<ApiResponse> actionGet(@PathVariable("id") long id) {
    return employeeService
        .getEmployeeById(id)
        .<ResponseEntity<ApiResponse>>map(
            employee -> ResponseEntity.ok(employeeMapper.toDto(employee)))
        .orElseGet(
            () ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApplicationErrorDto("Employee not found")));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public ResponseEntity<ApiResponse> actionDelete(@PathVariable("id") long id) {
    Author author = sessionEmployeeService.getAuthor();
    Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
    if (employeeOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApplicationErrorDto("Employee not found"));
    }
    ResultApp<Employee> result = employeeService.deleteEmployee(employeeOpt.get(), author);
    if (result instanceof ResultApp.Ok<Employee> ok) {
      return ResponseEntity.ok(employeeMapper.toDto(ok.value()));
    } else if (result instanceof ResultApp.InvalidData<Employee> invalid) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApplicationErrorDto(invalid.message()));
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @PostMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public ResponseEntity<ApiResponse> actionRestore(@PathVariable("id") long id) {
    Author author = sessionEmployeeService.getAuthor();
    Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
    if (employeeOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApplicationErrorDto("Employee not found"));
    }
    ResultApp<Employee> result = employeeService.restoreEmployee(employeeOpt.get(), author);
    if (result instanceof ResultApp.Ok<Employee> ok) {
      return ResponseEntity.ok(employeeMapper.toDto(ok.value()));
    } else if (result instanceof ResultApp.InvalidData<Employee> invalid) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApplicationErrorDto(invalid.message()));
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @PostMapping("/{id}/invite")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
  public ResponseEntity<EmployeeDto> actionInvite(@PathVariable("id") long id) {
    Author author = sessionEmployeeService.getAuthor();

    Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
    if (employeeOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Employee employee = employeeOpt.get();
    userService.createUserFromEmployee(employee, author);

    return ResponseEntity.ok(employeeMapper.toDto(employee));
  }

  @GetMapping("{id}/access")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
  public ResponseEntity<EmployeeAccess> actionGetAccess(@PathVariable("id") long id) {
    Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
    if (employeeOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Employee employee = employeeOpt.get();

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

    Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
    if (employeeOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Employee employee = employeeOpt.get();
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
