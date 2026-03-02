package com.open.crm.tenancy;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TenantSprngInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String tenantId = jwt.getClaimAsString("tenant_id");

            if (tenantId != null && !tenantId.isBlank()) {
                try {
                    UUID tenantUuid = UUID.fromString(tenantId);
                    TenantContext.setCurrentTenant(tenantUuid);
                    log.debug("Set tenant context from JWT: {}", tenantUuid);
                    return true;
                } catch (IllegalArgumentException e) {
                    log.error("Invalid tenant UUID in JWT: {}", tenantId, e);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid tenant in token");
                    return false;
                }
            } else {
                log.warn("Missing tenant_id claim in JWT token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Missing tenant_id in token");
                return false;
            }
        } else {
            log.info("No authentication or JWT token found, proceeding without tenant context");
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }
}
