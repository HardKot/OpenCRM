package com.open.crm.apiControllers.dto;

import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.admin.entities.user.UserRole;
import com.open.crm.core.entities.employee.AccessPermission;
import java.util.UUID;

public record TokenLoginUserResponse(
    UUID userId,
    UUID tenantId,
    String accessToken,
    String refreshToken,
    AccessPermission[] permissions,
    Long entityId,
    UserEntity entityName,
    UserRole role) {}
