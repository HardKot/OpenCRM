package com.open.crm.core.application.repositories;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.open.crm.core.entities.employee.Employee;

public interface IEmployeeRepository extends JpaRepository<Employee, Long>, PagingAndSortingRepository<Employee, Long> {
    long countByIsDeletedFalse();

    List<Employee> findAllByIsDeleted(PageRequest pageable, boolean isDeleted);
}
