package com.open.crm.controllers;

import com.open.crm.admin.entities.user.User;
import com.open.crm.components.mapper.IEmployeeMapper;
import com.open.crm.components.services.EmployeeUserService;
import com.open.crm.components.services.SessionService;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.application.results.ResultApp;
import com.open.crm.core.application.selectors.EmployeeSelector;
import com.open.crm.core.application.selectors.SortDirection;
import com.open.crm.core.application.services.EmployeeService;
import com.open.crm.core.entities.employee.AccessPermission;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.dto.ApiResponse;
import com.open.crm.dto.ApiSuggestDto;
import com.open.crm.dto.ApplicationErrorDto;
import com.open.crm.dto.EmployeeDto;
import com.open.crm.dto.EmployeeUserDto;
import com.open.crm.dto.PageResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
  private final EmployeeUserService employeeManagerFacades;
  private final SessionService sessionEmployeeService;
  private final IEmployeeMapper employeeMapper;
  private final IEmployeeRepository employeeRepository;

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
  public PageResponse actionGetAll(
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "100") int size,
      @RequestParam(name = "fullname", required = false) String fullname,
      @RequestParam(name = "position", required = false) String position,
      @RequestParam(name = "email", required = false) String email,
      @RequestParam(name = "phone", required = false) String phone,
      @RequestParam(name = "isDeleted", required = false, defaultValue = "false") boolean isDeleted,
      @RequestParam(name = "sortBy", required = false) String sortBy,
      @RequestParam(name = "sortDirection", required = false) String sortDirectionStr) {

    SortDirection sortDirection = SortDirection.valueOf(sortDirectionStr.toUpperCase());
    boolean showDeleted = isDeleted && sessionEmployeeService.isShowDeleted();
    EmployeeSelector selector = employeeService.getSelector();

    selector.setFullname(fullname);
    selector.setPosition(position);
    selector.setEmail(email);
    selector.setPhoneNumber(phone);
    selector.setIncludeDeleted(showDeleted);

    selector.setPage(page - 1);
    selector.setSize(size);
    selector.setSortBy(sortBy);
    selector.setSortDirection(sortDirection);

    ResultApp<EmployeeSelector> sResultApp = selector.search();

    switch (sResultApp) {
      case ResultApp.Ok<EmployeeSelector> ok -> {
        return new PageResponse.EmployeePageDto(
            selector.getTotalItems(),
            selector.getTotalPages(),
            selector.getItems().stream().map(employeeMapper::toDto).toArray(EmployeeDto[]::new));
      }

      case ResultApp.InvalidData<EmployeeSelector> invalidData -> {
        return new PageResponse.ErrorPageDto(invalidData.message());
      }

      default -> {
        return new PageResponse.ErrorPageDto("Unknown error");
      }
    }
  }

  @GetMapping("/position")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
  public ResponseEntity<ApiResponse> actionGetPositionsSuggest(
      @RequestParam(name = "name", required = false) String name) {
    List<String> positions = List.of();

    if (Objects.isNull(name) || name.trim().isEmpty()) {
      positions = employeeRepository.findAllPositions();
    } else {
      positions = employeeRepository.findPositionsByName(name);
    }
    return ResponseEntity.ok(new ApiSuggestDto<>(positions.toArray(new String[0])));
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

  @GetMapping("/{id}/form")
  public ResponseEntity<EmployeeUserDto> actionGetForm(@PathVariable("id") long id) {
    Optional<EmployeeUserDto> employeeFormOpt = employeeManagerFacades.getEmployeeUserById(id);
    if (employeeFormOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    EmployeeUserDto employeeUserDto = employeeFormOpt.get();
    if (!sessionEmployeeService.hasPermission(AccessPermission.EMPLOYEE_ACCESS)) {
      employeeUserDto = new EmployeeUserDto(employeeUserDto.employee());
    }

    return ResponseEntity.ok(employeeUserDto);
  }

  @PostMapping("/form")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public ResponseEntity<EmployeeUserDto> actionSaveEmployeeUser(
      @RequestBody EmployeeUserDto entity) {
    Author author = sessionEmployeeService.getAuthor();
    ResultApp<EmployeeUserDto> result = employeeManagerFacades.saveEmployeeUser(entity, author);

    switch (result) {
      case ResultApp.Ok<EmployeeUserDto> ok -> {
        EmployeeUserDto savedEntity = ok.value();
        if (!sessionEmployeeService.hasPermission(AccessPermission.EMPLOYEE_ACCESS)) {
          savedEntity = new EmployeeUserDto(savedEntity.employee());
        }
        return ResponseEntity.ok(savedEntity);
      }
      case ResultApp.InvalidData<EmployeeUserDto> invalidData -> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
      case ResultApp.NotFound<EmployeeUserDto> notFound -> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
      default -> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    }
  }

  @PutMapping("{id}/form")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public ResponseEntity<EmployeeUserDto> actionUpdateEmployeeUser(
      @PathVariable("id") long id, @RequestBody EmployeeUserDto formDto) {
    Employee entity = employeeMapper.toEntity(formDto.employee());
    entity.setId(id);
    User user = new User();
    user.setEnabled(formDto.isAccessAllowed());
    user.setPermissions(Set.of(formDto.permissions()));
    formDto = employeeMapper.toFormDto(entity, user);

    return actionSaveEmployeeUser(formDto);
  }
}
