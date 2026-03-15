package com.open.crm.core.application.selectors;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.entities.employee.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeSelector {
    private final IEmployeeRepository employeeRepository;

    public long countEmployees(boolean includeDeleted) {
        if (includeDeleted) {
            return employeeRepository.count();
        }
        return employeeRepository.countByIsDeletedFalse();
    }

    public List<Employee> getPageEmployees(int page, int size, boolean includeDeleted) {
        return employeeRepository
                .findAllByIsDeleted(PageRequest.of(page, size), includeDeleted);
    }

    public Optional<Employee> getEmployeeById(long id, boolean includeDeleted) {
        return employeeRepository.findById(id)
                .filter(employee -> includeDeleted || !employee.isDeleted());
    }
}
