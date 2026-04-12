package com.open.crm.core.application.repositories;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.employee.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface IEmployeeRepository extends IRepository<Employee> {
  Optional<Employee> findByEmail(String email);

  @Query("SELECT DISTINCT e.position FROM Employee e LIMIT 100")
  List<String> findAllPositions();

  @Query("SELECT DISTINCT e.position FROM Employee e WHERE e.position LIKE %:name% LIMIT 100")
  List<String> findPositionsByName(String name);
}
