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
import com.open.crm.core.application.selectors.EmployeeSelector;
import com.open.crm.core.application.services.EmployeeService;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

        private final EmployeeService employeeService;
        private final EmployeeSelector employeeSelector;

        private final UserService userService;

        private final SessionEmployeeService sessionEmployeeService;

        @PostMapping
        @Transactional
        @ResponseStatus(HttpStatus.CREATED)
        @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
        public Employee actionCreate(@RequestBody Employee employee) {
                Author author = sessionEmployeeService.getCurrentAuthor()
                                .orElseThrow(() -> new RuntimeException("Unauthorized"));

                Employee createdEmployee = employeeService.createEmployee(employee, author);

                return createdEmployee;
        }

        @GetMapping
        @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
        public ResponseEntity<List<Employee>> actionGetAll(
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "100") int size) {
                return ResponseEntity.ok(
                                employeeSelector.getPageEmployees(page, size, sessionEmployeeService.isShowDeleted()));
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
        public Employee actionUpdate(@PathVariable("id") long id, @RequestBody Employee employee) {
                Author author = sessionEmployeeService.getCurrentAuthor()
                                .orElseThrow(() -> new RuntimeException("Unauthorized"));

                employee.setId(id);
                return employeeService.updateEmployeeData(employee, author);
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
        public Employee actionGet(@PathVariable("id") long id) {
                return employeeSelector.getEmployeeById(id, sessionEmployeeService.isShowDeleted())
                                .orElseThrow(() -> new NotFoundException("Employee not found"));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
        public Employee actionDelete(@PathVariable("id") long id) {
                Author author = sessionEmployeeService.getCurrentAuthor()
                                .orElseThrow(() -> new RuntimeException("Unauthorized"));
                Employee employee = employeeSelector.getEmployeeById(id, true)
                                .orElseThrow(() -> new NotFoundException("Employee not found"));
                employee = employeeService.deleteEmployee(employee, author);
                return employee;
        }

        @PostMapping("/{id}")
        @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
        public Employee actionRestore(@PathVariable("id") long id) {
                Author author = sessionEmployeeService.getCurrentAuthor()
                                .orElseThrow(() -> new RuntimeException("Unauthorized"));
                Employee employee = employeeSelector.getEmployeeById(id, true)
                                .orElseThrow(() -> new NotFoundException("Employee not found"));
                employee = employeeService.restoreEmployee(employee, author);
                return employee;
        }

        @PostMapping("/{id}/invite")
        @ResponseStatus(HttpStatus.CREATED)
        @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
        public Employee actionInvite(@PathVariable("id") long id) {
                Author author = sessionEmployeeService.getCurrentAuthor()
                                .orElseThrow(() -> new RuntimeException("Unauthorized"));

                Employee employee = employeeSelector.getEmployeeById(id, true)
                                .orElseThrow(() -> new NotFoundException("Employee not found"));
                userService.createUserFromEmployee(employee, author);

                return employee;
        }

        @GetMapping("{id}/access")
        @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
        public EmployeeAccess actionGetAccess(@PathVariable("id") long id) {
                Employee employee = employeeSelector.getEmployeeById(id, sessionEmployeeService.isShowDeleted())
                                .orElseThrow(() -> new NotFoundException("Employee not found"));
                User user = userService.getUserByEmployee(employee);

                UserPermission[] permissions = user.getPermissions();

                return new EmployeeAccess(permissions);
        }

        @PutMapping("{id}/access")
        @PreAuthorize("hasPermission(null, 'EMPLOYEE_ACCESS')")
        public EmployeeAccess putMethodName(@PathVariable("id") long id,
                        @RequestBody EmployeeAccess entity) {
                Author author = sessionEmployeeService.getCurrentAuthor()
                                .orElseThrow(() -> new RuntimeException("Unauthorized"));

                Employee employee = employeeSelector.getEmployeeById(id, true)
                                .orElseThrow(() -> new NotFoundException("Employee not found"));
                User user = userService.updateUserPermissionsByEmployee(employee, entity.permissions(), author);

                return new EmployeeAccess(user.getPermissions());
        }

        @ExceptionHandler({ EmployeeException.class, UserException.class })
        public ResponseEntity<ApplicationErrorDto> handleEmployeeException(Exception ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApplicationErrorDto(ex.getMessage()));
        }

}
