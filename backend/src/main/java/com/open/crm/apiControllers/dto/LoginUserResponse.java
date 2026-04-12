package com.open.crm.apiControllers.dto;

import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.admin.entities.user.UserRole;
import com.open.crm.core.entities.employee.AccessPermission;
import java.util.UUID;

public record LoginUserResponse(
    boolean success,
    String message,
    UUID userId,
    UUID tenantId,
    AccessPermission[] permissions,
    Long entityId,
    UserEntity entityName,
    UserRole role) {}
