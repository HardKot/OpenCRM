package com.open.crm.apiControllers.dto;

import com.open.crm.core.entities.employee.AccessPermission;

public record EmployeeAccess(AccessPermission[] permissions) {}
