package com.open.crm.controllers.dto;

import com.open.crm.admin.entities.user.UserPermission;

public record EmployeeAccess(UserPermission[] permissions) {}
