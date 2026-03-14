package com.open.crm.controllers.dto;

import java.util.UUID;

import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.admin.entities.user.UserRole;

public record LoginUserResponse(boolean success, UUID userId, UUID tenantId, UserPermission[] permissions,
                Long entityId, UserEntity entityName, UserRole role) {

}
