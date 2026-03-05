package com.open.crm.core.application;

import org.springframework.stereotype.Service;

import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.domain.employee.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final IEmployeeRepository employeeRepository;

    public Employee createEmployee(Employee employee) {
        employee.setId(null);

        return employeeRepository.save(employee);
    }

}
