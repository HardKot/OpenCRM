package com.open.crm.dto;

import com.open.crm.admin.entities.user.UserRole;
import com.open.crm.core.entities.employee.AccessPermission;

public record EmployeeUserDto(
    EmployeeDto employee, AccessPermission[] permissions, boolean isAccessAllowed, UserRole role) {}
