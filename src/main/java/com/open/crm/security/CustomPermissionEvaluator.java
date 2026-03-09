package com.open.crm.security;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.open.crm.admin.entities.user.UserRole;

/**
 * Evaluates hasPermission() calls according to role hierarchy:
 *
 * ROLE_ADMIN — passes all permission checks unconditionally.
 * ROLE_OWNER — passes all regular permission checks (permissions are ignored),
 * but still cannot access ROLE_ADMIN-only endpoints.
 * ROLE_EMPLOYEE — access is gated by the explicit UserPermission set stored on
 * the user.
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            return false;
        }

        if (hasRole(authentication, UserRole.ROLE_ADMIN.name())) {
            return true;
        }

        if (hasRole(authentication, UserRole.ROLE_OWNER.name())) {
            return true;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(permission.toString()));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
            String targetType, Object permission) {
        return hasPermission(authentication, targetId, permission);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}
