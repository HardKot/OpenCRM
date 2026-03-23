package com.open.crm.apiControllers.dto;

import com.open.crm.admin.entities.user.UserPermission;

public record EmployeeAccess(UserPermission[] permissions) {}
