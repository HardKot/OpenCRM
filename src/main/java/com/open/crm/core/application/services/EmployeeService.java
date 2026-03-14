package com.open.crm.core.application.services;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.open.crm.core.application.errors.EmployeeException;
import com.open.crm.core.application.errors.NotFoundException;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.application.repositories.IInvestigationLogRepository;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import com.open.crm.core.entities.investigationLog.LogDetails;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeService {

    private final IEmployeeRepository employeeRepository;

    public Employee createEmployee(Employee employee) {
        employee.setId(null);
        employee.setCreatedAt(null);
        employee.setUpdatedAt(null);
        employee.setDeleted(false);

        employee = employeeRepository.save(employee);
        return employee;
    }

    public Employee updateEmployeeData(Employee employee) throws EmployeeException, NotFoundException {
        if (Objects.isNull(employee.getId())) {
            throw new EmployeeException("Employee ID cannot be null for update");
        }

        Employee existingEmployee = employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new NotFoundException("Employee not found with ID: " + employee.getId()));

        existingEmployee.setFirstname(employee.getFirstname());
        existingEmployee.setLastname(employee.getLastname());
        existingEmployee.setPatronymic(employee.getPatronymic());
        existingEmployee.setPosition(employee.getPosition());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setPhoneNumber(employee.getPhoneNumber());
        existingEmployee.setDeleted(false);

        employeeRepository.save(existingEmployee);

        return existingEmployee;
    }

    public Employee deleteEmployee(Employee employee) throws EmployeeException, NotFoundException {
        if (employee.isDeleted()) {
            throw new EmployeeException("Employee with ID: " + employee.getId() + " is already deleted");
        }

        employee.setDeleted(true);
        employeeRepository.save(employee);

        return employee;
    }

    public Employee restoreEmployee(Employee employee) throws EmployeeException, NotFoundException {
        if (!employee.isDeleted()) {
            throw new EmployeeException("Employee with ID: " + employee.getId() + " is not deleted");
        }
        employee.setDeleted(false);
        employeeRepository.save(employee);
        return employee;
    }

}
