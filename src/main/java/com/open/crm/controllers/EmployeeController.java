package com.open.crm.controllers;

import com.open.crm.admin.application.UserService;
import com.open.crm.admin.application.exceptions.UserException;
import com.open.crm.admin.entities.user.User;
import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.components.services.SessionEmployeeService;
import com.open.crm.controllers.dto.ApplicationErrorDto;
import com.open.crm.controllers.dto.EmployeeAccess;
import com.open.crm.core.application.EmployeeService;
import com.open.crm.core.application.errors.EmployeeException;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import com.open.crm.core.entities.investigationLog.LogDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    private final UserService userService;

    private final SessionEmployeeService sessionEmployeeService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Employee> actionCreate(@RequestBody Employee employee) {
        Optional<Employee> currentEmployeeOpt = sessionEmployeeService.getCurrent();
        if (currentEmployeeOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(employee, currentEmployeeOpt.get()));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
    public ResponseEntity<List<Employee>> actionGetAll() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Employee> actionUpdate(@PathVariable("id") long id, @RequestBody Employee employee) {
        Optional<Employee> currentEmployeeOpt = sessionEmployeeService.getCurrent();
        if (currentEmployeeOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        employee.setId(id);
        return ResponseEntity.ok(employeeService.updateEmployeeData(employee, currentEmployeeOpt.get()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
    public ResponseEntity<Employee> actionGet(@PathVariable("id") long id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Employee> actionDelete(@PathVariable("id") long id) {
        Optional<Employee> currentEmployeeOpt = sessionEmployeeService.getCurrent();
        if (currentEmployeeOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Employee employee = employeeService.deleteEmployee(id, currentEmployeeOpt.get());
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Employee> actionRestore(@PathVariable("id") long id) {
        Optional<Employee> currentEmployeeOpt = sessionEmployeeService.getCurrent();
        if (currentEmployeeOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Employee employee = employeeService.restoreEmployee(id, currentEmployeeOpt.get());
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/{id}/invite")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
    public ResponseEntity<Employee> actionInvite(@PathVariable("id") long id) {
        Optional<Employee> currentEmployeeOpt = sessionEmployeeService.getCurrent();
        if (currentEmployeeOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Employee employee = employeeService.getEmployee(id);
        userService.createUserFromEmployee(employee);

        InvestigationLog log = new InvestigationLog();
        log.setEmployee(currentEmployeeOpt.get());
        log.setTenantId(currentEmployeeOpt.get().getTenantId());
        log.setDetails(LogDetails.builder().action("INVITE").entityName("EMPLOYEE").entityId(employee.getId()).build());

        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @GetMapping("{id}/access")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
    public ResponseEntity<EmployeeAccess> actionGetAccess(@PathVariable("id") long id) {
        Employee employee = employeeService.getEmployee(id);

        User user = userService.getUserByEmployee(employee);

        UserPermission[] permissions = user.getPermissions();

        return ResponseEntity.ok(new EmployeeAccess(permissions));
    }

    @PutMapping("{id}/access")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
    public ResponseEntity<EmployeeAccess> putMethodName(@PathVariable("id") long id,
            @RequestBody EmployeeAccess entity) {
        Optional<Employee> currentEmployeeOpt = sessionEmployeeService.getCurrent();
        if (currentEmployeeOpt.isEmpty())
            return ResponseEntity.status(401).build();

        Employee employee = employeeService.getEmployee(id);
        User user = userService.getUserByEmployee(employee);
        user = userService.updateUserPermissions(user.getId(), entity.permissions());

        InvestigationLog log = new InvestigationLog();
        log.setEmployee(currentEmployeeOpt.get());
        log.setTenantId(currentEmployeeOpt.get().getTenantId());
        log.setDetails(
                LogDetails.builder().action("UPDATE_ACCESS").entityName("EMPLOYEE").entityId(employee.getId()).build());

        return ResponseEntity.ok(new EmployeeAccess(user.getPermissions()));
    }

    @ExceptionHandler({ EmployeeException.class, UserException.class })
    public ResponseEntity<ApplicationErrorDto> handleEmployeeException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApplicationErrorDto(ex.getMessage()));
    }

}
