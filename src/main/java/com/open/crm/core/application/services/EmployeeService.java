package com.open.crm.core.application.services;

import com.open.crm.core.application.IUserService;
import com.open.crm.core.application.InvestigationLogCreator;
import com.open.crm.core.application.errors.EmployeeException;
import com.open.crm.core.application.errors.NotFoundException;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final IEmployeeRepository employeeRepository;
  private final InvestigationLogCreator investigationLogCreator;

  private final InvestigationLogService investigationLogService;
  ;
  private final IUserService userService;

  @Qualifier("employeeSelectorData") @Getter
  private final SelectorData<Employee> employeeSelector;

  @Transactional
  public Employee createEmployee(Employee employee, Author author) {
    employee.setId(null);
    employee.setCreatedAt(null);
    employee.setUpdatedAt(null);
    employee.setDeleted(false);

    employee = employeeRepository.save(employee);
    InvestigationLog log = investigationLogCreator.createEmployeeLog(employee, author);
    investigationLogService.saveLog(log);
    return employee;
  }

  @Transactional
  public Employee updateEmployeeData(Employee employee, Author author)
      throws EmployeeException, NotFoundException {
    if (Objects.isNull(employee.getId())) {
      throw new EmployeeException("Employee ID cannot be null for update");
    }

    Employee existingEmployee =
        employeeRepository
            .findById(employee.getId())
            .orElseThrow(
                () -> new NotFoundException("Employee not found with ID: " + employee.getId()));

    existingEmployee.setFirstname(employee.getFirstname());
    existingEmployee.setLastname(employee.getLastname());
    existingEmployee.setPatronymic(employee.getPatronymic());
    existingEmployee.setPosition(employee.getPosition());
    existingEmployee.setPhoneNumber(employee.getPhoneNumber());
    existingEmployee.setDeleted(false);

    employeeRepository.save(existingEmployee);

    InvestigationLog log = investigationLogCreator.updateEmployeeLog(existingEmployee, author);
    investigationLogService.saveLog(log);

    return existingEmployee;
  }

  public Employee updateEmail(Employee employee, String email, Author author)
      throws EmployeeException, NotFoundException {
    if (Objects.isNull(employee.getId())) {
      throw new EmployeeException("Employee ID cannot be null for update");
    }

    Employee existingEmployee =
        employeeRepository
            .findById(employee.getId())
            .orElseThrow(
                () -> new NotFoundException("Employee not found with ID: " + employee.getId()));

    existingEmployee.setEmail(email);

    employeeRepository.save(existingEmployee);
    userService.updateUserEmail(existingEmployee, email);

    InvestigationLog log = investigationLogCreator.updateEmployeeLog(existingEmployee, author);
    investigationLogService.saveLog(log);

    return existingEmployee;
  }

  public Employee deleteEmployee(Employee employee, Author author)
      throws EmployeeException, NotFoundException {
    if (employee.isDeleted()) {
      throw new EmployeeException("Employee with ID: " + employee.getId() + " is already deleted");
    }

    employee.setDeleted(true);
    employeeRepository.save(employee);

    InvestigationLog log = investigationLogCreator.updateEmployeeLog(employee, author);
    userService.disabledByEmployee(employee);
    investigationLogService.saveLog(log);

    return employee;
  }

  public Employee restoreEmployee(Employee employee, Author author)
      throws EmployeeException, NotFoundException {
    if (!employee.isDeleted()) {
      throw new EmployeeException("Employee with ID: " + employee.getId() + " is not deleted");
    }
    employee.setDeleted(false);
    employeeRepository.save(employee);
    InvestigationLog log = investigationLogCreator.updateEmployeeLog(employee, author);
    userService.enabledByEmployee(employee);
    investigationLogService.saveLog(log);
    return employee;
  }

  public Employee getEmployeeById(Long id) throws NotFoundException {
    return employeeRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Employee not found with ID: " + id));
  }
}
