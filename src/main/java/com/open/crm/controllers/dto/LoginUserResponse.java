package com.open.crm.controllers.dto;

import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.admin.entities.user.UserRole;
import java.util.UUID;

public record LoginUserResponse(
    boolean success,
    UUID userId,
    UUID tenantId,
    UserPermission[] permissions,
    Long entityId,
    UserEntity entityName,
    UserRole role) {}
