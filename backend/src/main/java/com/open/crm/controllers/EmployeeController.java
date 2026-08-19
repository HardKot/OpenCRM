package com.open.crm.controllers;

import com.open.crm.admin.entities.user.User;
import com.open.crm.components.mapper.IEmployeeMapper;
import com.open.crm.components.services.EmployeeUserService;
import com.open.crm.components.services.SessionService;
import com.open.crm.core.application.errors.EmployeeException;
import com.open.crm.core.application.errors.NotFoundException;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.application.selectors.EmployeeSelector;
import com.open.crm.core.application.selectors.SortDirection;
import com.open.crm.core.application.services.EmployeeService;
import com.open.crm.core.entities.employee.AccessPermission;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.dto.common.ApiResponse;
import com.open.crm.dto.common.ApiSuggestDto;
import com.open.crm.dto.common.PageResponse;
import com.open.crm.dto.employee.EmployeeDto;
import com.open.crm.dto.employee.EmployeeSearchCriteria;
import com.open.crm.dto.employee.EmployeeUserDto;
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

  private static final String EMPLOYEE_NOT_FOUND_MESSAGE = "Employee not found";

  private final EmployeeService employeeService;
  private final EmployeeUserService employeeManagerFacades;
  private final SessionService sessionEmployeeService;
  private final IEmployeeMapper employeeMapper;
  private final IEmployeeRepository employeeRepository;

  @PostMapping
  @Transactional
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public ResponseEntity<EmployeeDto> actionCreate(@RequestBody EmployeeDto employee) {
    Author author = sessionEmployeeService.getAuthor();

    Employee createdEmployee =
        employeeService.createEmployee(employeeMapper.toEntity(employee), author);

    return ResponseEntity.status(HttpStatus.CREATED).body(employeeMapper.toDto(createdEmployee));
  }

  @GetMapping
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
  public PageResponse<EmployeeDto> actionGetAll(@ModelAttribute EmployeeSearchCriteria criteria) {
    SortDirection sortDirection;
    try {
      sortDirection =
          criteria.sortDirection() != null
              ? SortDirection.valueOf(criteria.sortDirection().toUpperCase())
              : SortDirection.ASC;
    } catch (IllegalArgumentException e) {
      sortDirection = SortDirection.ASC;
    }

    boolean showDeleted =
        Boolean.TRUE.equals(criteria.isDeleted()) && sessionEmployeeService.isShowDeleted();
    EmployeeSelector selector = employeeService.getSelector();

    selector.setFullname(criteria.fullname());
    selector.setPosition(criteria.position());
    selector.setEmail(criteria.email());
    selector.setPhoneNumber(criteria.phone());
    selector.setIncludeDeleted(showDeleted);

    selector.setPage(criteria.page() != null ? criteria.page() - 1 : 0);
    selector.setSize(criteria.size() != null ? criteria.size() : 100);
    selector.setSortBy(criteria.sortBy());
    selector.setSortDirection(sortDirection);

    selector.search();

    return new PageResponse.EmployeePageDto(
        selector.getTotalItems(),
        selector.getTotalPages(),
        selector.getItems().stream().map(employeeMapper::toDto).toArray(EmployeeDto[]::new));
  }

  @GetMapping("/position")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
  public ResponseEntity<ApiResponse> actionGetPositionsSuggest(
      @RequestParam(name = "name", required = false) String name) {
    List<String> positions;

    if (Objects.isNull(name) || name.trim().isEmpty()) {
      positions = employeeRepository.findAllPositions();
    } else {
      positions = employeeRepository.findPositionsByName(name);
    }
    return ResponseEntity.ok(new ApiSuggestDto<>(positions.toArray(new String[0])));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public ResponseEntity<EmployeeDto> actionUpdate(
      @PathVariable("id") long id, @RequestBody EmployeeDto data)
      throws NotFoundException, EmployeeException {
    Author author = sessionEmployeeService.getAuthor();
    Employee employee = employeeMapper.toEntity(data);
    employee.setId(id);
    employee = employeeService.updateEmployeeData(employee, author);

    if (!data.email().equals(employee.getEmail())) {
      employee = employeeService.updateEmail(employee, data.email(), author);
    }

    return ResponseEntity.ok(employeeMapper.toDto(employee));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
  public ResponseEntity<EmployeeDto> actionGet(@PathVariable("id") long id)
      throws NotFoundException {
    return employeeService
        .getEmployeeById(id)
        .<ResponseEntity<EmployeeDto>>map(
            employee -> ResponseEntity.ok(employeeMapper.toDto(employee)))
        .orElseThrow(() -> new NotFoundException(EMPLOYEE_NOT_FOUND_MESSAGE));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public ResponseEntity<EmployeeDto> actionDelete(@PathVariable("id") long id)
      throws NotFoundException, EmployeeException {
    Author author = sessionEmployeeService.getAuthor();
    Employee employee =
        employeeService
            .getEmployeeById(id)
            .orElseThrow(() -> new NotFoundException(EMPLOYEE_NOT_FOUND_MESSAGE));

    employee = employeeService.deleteEmployee(employee, author);

    return ResponseEntity.ok(employeeMapper.toDto(employee));
  }

  @PostMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public ResponseEntity<EmployeeDto> actionRestore(@PathVariable("id") long id) {
    Author author = sessionEmployeeService.getAuthor();
    Employee employee =
        employeeService
            .getEmployeeById(id)
            .orElseThrow(() -> new NotFoundException(EMPLOYEE_NOT_FOUND_MESSAGE));
    employee = employeeService.restoreEmployee(employee, author);

    return ResponseEntity.ok(employeeMapper.toDto(employee));
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
    if (!sessionEmployeeService.hasPermission(AccessPermission.EMPLOYEE_ACCESS)) {
      entity = new EmployeeUserDto(entity.employee());
    }

    entity = employeeManagerFacades.saveEmployeeUser(entity, author);

    if (!sessionEmployeeService.hasPermission(AccessPermission.EMPLOYEE_ACCESS)) {
      entity = new EmployeeUserDto(entity.employee());
    }

    return ResponseEntity.ok(entity);
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
