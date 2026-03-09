package com.open.crm.core.application;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.application.repositories.IInvestigationLogRepository;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import com.open.crm.core.entities.investigationLog.LogDetails;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final IEmployeeRepository employeeRepository;
    private final IInvestigationLogRepository investigationLogRepository;
    private final ISessionEmployee sessionEmployee;

    public Employee createEmployee(Employee employee) {
        Employee author = sessionEmployee.getCurrent()
                .orElseThrow(() -> new IllegalStateException("No authenticated employee found in session"));

        employee.setId(null);
        employee.setTenantId(author.getTenantId());
        employee.setCreatedAt(null);
        employee.setUpdatedAt(null);
        employee.setDeleted(false);

        employee = employeeRepository.save(employee);
        InvestigationLog log = getCreateEmployeeLog(employee, author);
        investigationLogRepository.save(log);
        return employee;
    }

    @Transactional
    public Employee updateEmployeeData(Employee employee) {
        Employee author = sessionEmployee.getCurrent()
                .orElseThrow(() -> new IllegalStateException("No authenticated employee found in session"));

        if (Objects.isNull(employee.getId())) {
            throw new IllegalArgumentException("Employee ID cannot be null for update");
        }

        InvestigationLog log = getUpdateEmployeeLog(employee, author);
        Employee existingEmployee = employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employee.getId()));

        existingEmployee.setFirstname(employee.getFirstname());
        existingEmployee.setLastname(employee.getLastname());
        existingEmployee.setPatronymic(employee.getPatronymic());
        existingEmployee.setPosition(employee.getPosition());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setPhoneNumber(employee.getPhoneNumber());
        existingEmployee.setDeleted(false);

        employeeRepository.save(existingEmployee);
        investigationLogRepository.save(log);

        return existingEmployee;
    }

    @Transactional
    public Employee deleteEmployee(Long id) {
        Employee author = sessionEmployee.getCurrent()
                .orElseThrow(() -> new IllegalStateException("No authenticated employee found in session"));

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));

        InvestigationLog log = getDeleteEmployeeLog(employee, author);
        employee.setDeleted(true);
        employeeRepository.save(employee);
        investigationLogRepository.save(log);

        return employee;
    }

    @Transactional
    public Employee restoreEmployee(Long id) {
        Employee author = sessionEmployee.getCurrent()
                .orElseThrow(() -> new IllegalStateException("No authenticated employee found in session"));

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));
        InvestigationLog log = getRestoreEmployeeLog(employee, author);
        employee.setDeleted(false);
        employeeRepository.save(employee);
        investigationLogRepository.save(log);
        return employee;
    }

    public Employee getEmployee(Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    private InvestigationLog getCreateEmployeeLog(Employee employee, Employee author) {
        InvestigationLog log = new InvestigationLog();
        log.setEmployee(author);
        log.setTenantId(author.getTenantId());
        log.setDetails(LogDetails.builder()
                .action("CREATE")
                .entityName("EMPLOYEE")
                .entityId(employee.getId())
                .build());
        return log;
    }

    private InvestigationLog getUpdateEmployeeLog(Employee employee, Employee author) {
        InvestigationLog log = new InvestigationLog();
        log.setEmployee(author);
        log.setTenantId(author.getTenantId());
        log.setDetails(LogDetails.builder()
                .action("UPDATE")
                .entityName("EMPLOYEE")
                .entityId(employee.getId())
                .build());
        return log;
    }

    private InvestigationLog getDeleteEmployeeLog(Employee employee, Employee author) {
        InvestigationLog log = new InvestigationLog();
        log.setEmployee(author);
        log.setTenantId(author.getTenantId());
        log.setDetails(LogDetails.builder()
                .action("DELETE")
                .entityName("EMPLOYEE")
                .entityId(employee.getId())
                .build());
        return log;
    }

    private InvestigationLog getRestoreEmployeeLog(Employee employee, Employee author) {
        InvestigationLog log = new InvestigationLog();
        log.setEmployee(author);
        log.setTenantId(author.getTenantId());
        log.setDetails(LogDetails.builder()
                .action("RESTORE")
                .entityName("EMPLOYEE")
                .entityId(employee.getId())
                .build());
        return log;
    }
}
