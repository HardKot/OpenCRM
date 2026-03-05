package com.open.crm.core.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.open.crm.core.domain.employee.Employee;

public interface IEmployeeRepository extends JpaRepository<Employee, Long> {

}
