package com.open.crm.core.application.services;

import com.open.crm.core.application.IUserService;
import com.open.crm.core.application.errors.EmployeeException;
import com.open.crm.core.application.errors.NotFoundException;
import com.open.crm.core.application.investigation.events.*;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.application.selectors.EmployeeSelector;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final IEmployeeRepository employeeRepository;
  private final IUserService userService;
  private final ApplicationEventPublisher eventPublisher;

  @Lookup
  public EmployeeSelector getSelector() {
    return new EmployeeSelector(employeeRepository);
  }

  @Transactional
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public Employee createEmployee(Employee employee, Author author) {
    employee.setId(null);
    employee.setCreatedAt(null);
    employee.setUpdatedAt(null);
    employee.setDeleted(false);

    employee = employeeRepository.save(employee);
    eventPublisher.publishEvent(new CreateEmployeeEvent(employee, author));

    return employee;
  }

  @Transactional
  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public Employee updateEmployeeData(Employee employee, Author author)
      throws EmployeeException, NotFoundException {
    if (Objects.isNull(employee.getId())) {
      throw new EmployeeException("Employee ID cannot be null for update");
    }

    Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employee.getId());
    if (existingEmployeeOpt.isEmpty()) throw new NotFoundException("Employee not found");

    Employee existingEmployee = existingEmployeeOpt.get();

    existingEmployee.setFirstname(employee.getFirstname());
    existingEmployee.setLastname(employee.getLastname());
    existingEmployee.setPatronymic(employee.getPatronymic());
    existingEmployee.setPosition(employee.getPosition());
    existingEmployee.setPhoneNumber(employee.getPhoneNumber());
    existingEmployee.setDeleted(false);

    employeeRepository.save(existingEmployee);

    eventPublisher.publishEvent(new UpdateEmployeeEvent(existingEmployee, author));

    return existingEmployee;
  }

  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public Employee updateEmail(Employee employee, String email, Author author)
      throws EmployeeException, NotFoundException {
    if (Objects.isNull(employee.getId())) {
      throw new EmployeeException("Employee ID cannot be null for update");
    }

    Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employee.getId());
    if (existingEmployeeOpt.isEmpty()) {
      throw new NotFoundException("Employee not found");
    }
    Employee existingEmployee = existingEmployeeOpt.get();

    existingEmployee.setEmail(email);

    employeeRepository.save(existingEmployee);
    userService.updateUserEmail(existingEmployee, email);

    eventPublisher.publishEvent(new UpdateEmployeeEvent(existingEmployee, author));

    return existingEmployee;
  }

  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public Employee deleteEmployee(Employee employee, Author author) throws NotFoundException {
    if (employee.isDeleted()) {
      throw new NotFoundException("Employee with ID: " + employee.getId() + " is already deleted");
    }

    employee.setDeleted(true);
    employeeRepository.save(employee);

    eventPublisher.publishEvent(new DeleteEmployeeEvent(employee, author));
    userService.disabledByEmployee(employee);

    return employee;
  }

  @PreAuthorize("hasPermission(null, 'EMPLOYEE_UPDATE')")
  public Employee restoreEmployee(Employee employee, Author author) throws NotFoundException {
    if (!employee.isDeleted()) {
      throw new NotFoundException("Employee with ID: " + employee.getId() + " is not deleted");
    }
    employee.setDeleted(false);
    employeeRepository.save(employee);
    eventPublisher.publishEvent(new RestoreEmployeeEvent(employee, author));
    userService.enabledByEmployee(employee);
    return employee;
  }

  @PreAuthorize("hasPermission(null, 'EMPLOYEE_READ')")
  public Optional<Employee> getEmployeeById(Long id) {
    return employeeRepository.findById(id);
  }
}
