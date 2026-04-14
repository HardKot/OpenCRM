package com.open.crm.components.services;

import com.open.crm.admin.application.UserService;
import com.open.crm.admin.entities.user.User;
import com.open.crm.components.mapper.IEmployeeMapper;
import com.open.crm.core.application.results.ResultApp;
import com.open.crm.core.application.services.EmployeeService;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.dto.EmployeeUserDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeUserService {
  private final EmployeeService employeeService;
  private final UserService userService;

  private final IEmployeeMapper employeeMapper;
  private final SaveEmployeeUserUseCase saveEmployeeUserUseCase;

  public Optional<EmployeeUserDto> getEmployeeUserById(long id) {
    Optional<Employee> employeeOpt = employeeService.getEmployeeById(id);
    if (employeeOpt.isEmpty()) return Optional.empty();
    Optional<User> userOpt = userService.getUserByEmployee(employeeOpt.get());

    return Optional.of(employeeMapper.toFormDto(employeeOpt.get(), userOpt.orElse(null)));
  }

  public ResultApp<EmployeeUserDto> saveEmployeeUser(EmployeeUserDto form, Author author) {
    return saveEmployeeUserUseCase.execute(form, author);
  }
}
