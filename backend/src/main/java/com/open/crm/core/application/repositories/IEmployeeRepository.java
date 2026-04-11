package com.open.crm.core.application.repositories;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.employee.Employee;
import java.util.Optional;

public interface IEmployeeRepository
    extends IRepository<Employee> {
  Optional<Employee> findByEmail(String email);
}
