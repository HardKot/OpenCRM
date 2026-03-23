package com.open.crm.apiControllers.dto;

import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.admin.entities.user.UserRole;
import java.util.UUID;

public record TokenLoginUserResponse(
    UUID userId,
    UUID tenantId,
    String accessToken,
    String refreshToken,
    UserPermission[] permissions,
    Long entityId,
    UserEntity entityName,
    UserRole role) {}
