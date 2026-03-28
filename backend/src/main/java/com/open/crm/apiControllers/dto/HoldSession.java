package com.open.crm.apiControllers.dto;

import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.admin.entities.user.UserRole;
import java.util.UUID;

public sealed interface HoldSession<T> permits HoldSession.Employee {
  UUID userId();

  UUID tenantId();

  UserPermission[] permissions();

  UserRole role();

  T entity();

  public record Employee(
      UUID userId, UUID tenantId, UserPermission[] permissions, UserRole role, EmployeeDto entity)
      implements HoldSession<EmployeeDto> {}
}
