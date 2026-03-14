package com.open.crm.controllers;

import com.open.crm.admin.application.UserService;
import com.open.crm.admin.application.exceptions.UserException;
import com.open.crm.admin.entities.user.User;
import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.components.services.SessionEmployeeService;
import com.open.crm.controllers.dto.ApplicationErrorDto;
import com.open.crm.controllers.dto.EmployeeAccess;
import com.open.crm.core.application.errors.EmployeeException;
import com.open.crm.core.application.errors.NotFoundException;
import com.open.crm.core.application.investigationLog.EmployeeInvestigationLog;
import com.open.crm.core.application.selectors.EmployeeSelector;
import com.open.crm.core.application.services.EmployeeService;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import com.open.crm.core.entities.investigationLog.LogDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeSelector employeeSelector;
    private final EmployeeInvestigationLog employeeInvestigationLog;

    private final UserService userService;

    private final SessionEmployeeService sessionEmployeeService;

    @PostMapping
    @Transactional
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Employee> actionCreate(@RequestBody Employee employee) {
        Author author = sessionEmployeeService.getCurrentAuthor()
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        Employee createdEmployee = employeeService.createEmployee(employee);
        employeeInvestigationLog.createEmployeeLog(createdEmployee, author);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(employee));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
    public ResponseEntity<List<Employee>> actionGetAll(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        return ResponseEntity.ok(employeeSelector.getPageEmployees(page, size, sessionEmployeeService.isShowDeleted()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Employee> actionUpdate(@PathVariable("id") long id, @RequestBody Employee employee) {
        Author author = sessionEmployeeService.getCurrentAuthor()
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        employee.setId(id);
        employeeInvestigationLog.updateEmployeeLog(employee, author);
        return ResponseEntity.ok(employeeService.updateEmployeeData(employee));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
    public ResponseEntity<Employee> actionGet(@PathVariable("id") long id) {
        return employeeSelector.getEmployeeById(id, sessionEmployeeService.isShowDeleted()).map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Employee> actionDelete(@PathVariable("id") long id) {
        Author author = sessionEmployeeService.getCurrentAuthor()
                .orElseThrow(() -> new RuntimeException("Unauthorized"));
        Employee employee = employeeSelector.getEmployeeById(id, sessionEmployeeService.isShowDeleted())
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        employee = employeeService.deleteEmployee(employee);
        employeeInvestigationLog.updateEmployeeLog(employee, author);
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Employee> actionRestore(@PathVariable("id") long id) {
        Author author = sessionEmployeeService.getCurrentAuthor()
                .orElseThrow(() -> new RuntimeException("Unauthorized"));
        Employee employee = employeeSelector.getEmployeeById(id, sessionEmployeeService.isShowDeleted())
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        employee = employeeService.restoreEmployee(employee);
        employeeInvestigationLog.updateEmployeeLog(employee, author);
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/{id}/invite")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
    public ResponseEntity<Employee> actionInvite(@PathVariable("id") long id) {
        Author author = sessionEmployeeService.getCurrentAuthor()
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        Employee employee = employeeSelector.getEmployeeById(id, sessionEmployeeService.isShowDeleted())
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        userService.createUserFromEmployee(employee);

        employeeInvestigationLog.inviteEmployeeLog(employee, author);

        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @GetMapping("{id}/access")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
    public ResponseEntity<EmployeeAccess> actionGetAccess(@PathVariable("id") long id) {
        Employee employee = employeeSelector.getEmployeeById(id, sessionEmployeeService.isShowDeleted())
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        User user = userService.getUserByEmployee(employee);

        UserPermission[] permissions = user.getPermissions();

        return ResponseEntity.ok(new EmployeeAccess(permissions));
    }

    @PutMapping("{id}/access")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
    public ResponseEntity<EmployeeAccess> putMethodName(@PathVariable("id") long id,
            @RequestBody EmployeeAccess entity) {
        Author author = sessionEmployeeService.getCurrentAuthor()
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        Employee employee = employeeSelector.getEmployeeById(id, sessionEmployeeService.isShowDeleted())
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        User user = userService.getUserByEmployee(employee);
        user = userService.updateUserPermissions(user.getId(), entity.permissions());

        employeeInvestigationLog.updateAccessEmployeeLog(employee, author);

        return ResponseEntity.ok(new EmployeeAccess(user.getPermissions()));
    }

    @ExceptionHandler({ EmployeeException.class, UserException.class })
    public ResponseEntity<ApplicationErrorDto> handleEmployeeException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApplicationErrorDto(ex.getMessage()));
    }

}
