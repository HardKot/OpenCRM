package com.open.crm.core.application;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.entities.employee.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final IEmployeeRepository employeeRepository;

    public Employee createEmployee(Employee employee) {
        employee.setId(null);

        return employeeRepository.save(employee);
    }

    public Employee updateEmployeeData(Employee employee) {
        if (Objects.isNull(employee.getId())) {
            throw new IllegalArgumentException("Employee ID cannot be null for update");
        }

        Employee existingEmployee = employeeRepository.findById(employee.getId())
            .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employee.getId()));

        existingEmployee.setFirstname(employee.getFirstname());
        existingEmployee.setLastname(employee.getLastname());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setPhoneNumber(employee.getPhoneNumber());

        existingEmployee.setPosition(null);

        return employeeRepository.save(employee);
    }

}
