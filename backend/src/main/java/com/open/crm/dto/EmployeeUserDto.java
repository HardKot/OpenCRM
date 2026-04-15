package com.open.crm.dto;

import com.open.crm.admin.entities.user.UserRole;
import com.open.crm.core.entities.employee.AccessPermission;

public record EmployeeUserDto(
    EmployeeDto employee, AccessPermission[] permissions, Boolean isAccessAllowed, UserRole role) {
  public EmployeeUserDto(EmployeeDto employee) {
    this(employee, null, null, null);
  }
}
