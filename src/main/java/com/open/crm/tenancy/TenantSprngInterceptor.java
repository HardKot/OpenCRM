package com.open.crm.tenancy;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.open.crm.admin.application.interfaces.IDatabase;
import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.security.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantSprngInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;
    private final IDatabase database;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || !(authentication instanceof Jwt)) {
            log.debug("No authentication or not a JWT token, skipping tenant context setup");
            return true;
        }

        Optional<Tenant> tenantOpt = tokenService.getTenantIdFromToken((Jwt) authentication);

        if (tenantOpt.isEmpty()) {
            log.warn("Tenant not found in token claims");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid tenant in token");
            return false;
        }

        database.setContextTenant(tenantOpt.get());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        database.clearContextTenant();
    }

}
