package com.open.crm.dto;

import com.open.crm.admin.entities.user.UserRole;
import com.open.crm.core.entities.employee.AccessPermission;
import java.util.UUID;

public sealed interface HoldSession<T> permits HoldSession.Employee {
  UUID userId();

  UUID tenantId();

  AccessPermission[] permissions();

  UserRole role();

  T entity();

  public record Employee(
      UUID userId, UUID tenantId, AccessPermission[] permissions, UserRole role, EmployeeDto entity)
      implements HoldSession<EmployeeDto> {}
}
