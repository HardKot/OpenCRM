package com.open.crm.core.application.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.employee.Employee;

public interface IEmployeeRepository extends IRepository<Employee> {
    Optional<Employee> findByEmail(String email);

    long countByIsDeleted(boolean isDeleted);

    List<Employee> findAllByIsDeleted(PageRequest pageable, boolean isDeleted);
}
