package com.open.crm.dto.employee;

import com.open.crm.core.entities.employee.AccessPermission;

public record EmployeeAccess(AccessPermission[] permissions) {}
