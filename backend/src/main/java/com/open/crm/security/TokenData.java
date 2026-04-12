package com.open.crm.security;

import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.admin.entities.user.UserRole;
import com.open.crm.core.entities.employee.AccessPermission;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;

public record TokenData(
    Jwt accessToken,
    Jwt refreshToken,
    UUID userId,
    UUID tenantId,
    AccessPermission[] permissions,
    Long entityId,
    UserEntity entityName,
    UserRole role) {}
