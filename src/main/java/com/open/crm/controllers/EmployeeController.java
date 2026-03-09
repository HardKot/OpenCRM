package com.open.crm.controllers;

import com.open.crm.core.application.EmployeeService;
import com.open.crm.core.entities.employee.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        employee.setId(id);
        return ResponseEntity.ok(employeeService.updateEmployeeData(employee));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
    public ResponseEntity<Employee> get(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
