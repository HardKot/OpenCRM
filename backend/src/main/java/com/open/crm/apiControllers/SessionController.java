package com.open.crm.apiControllers;

import com.open.crm.admin.entities.user.User;
import com.open.crm.apiControllers.dto.HoldSession;
import com.open.crm.components.mapper.IEmployeeMapper;
import com.open.crm.components.services.SessionService;
import com.open.crm.core.entities.employee.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {
  private final SessionService sessionEmployeeService;
  private final IEmployeeMapper employeeMapper;

  @GetMapping("/hold")
  public ResponseEntity<HoldSession.Employee> actionHold() {
    User user = sessionEmployeeService.getUser();
    Employee employee = sessionEmployeeService.getEmployee();

    HoldSession.Employee holdSession =
        new HoldSession.Employee(
            user.getId(),
            user.getTenant().getId(),
            user.getPermissions(),
            user.getRole(),
            employeeMapper.toDto(employee));

    return ResponseEntity.ok(holdSession);
  }
}
