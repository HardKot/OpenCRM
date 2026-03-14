package com.open.crm.security;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;

import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.admin.entities.user.UserPermission;
import com.open.crm.admin.entities.user.UserRole;

public record TokenData(Jwt accessToken, Jwt refreshToken, UUID userId, UUID tenantId, UserPermission[] permissions,
                Long entityId, UserEntity entityName, UserRole role) {

}
