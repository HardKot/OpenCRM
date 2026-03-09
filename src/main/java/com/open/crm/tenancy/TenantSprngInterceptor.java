package com.open.crm.tenancy;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.open.crm.admin.application.interfaces.IDatabase;
import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.admin.entities.user.User;
import com.open.crm.security.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantSprngInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;
    private final ITenantRepository tenantRepository;
    private final IDatabase database;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            log.debug("No authentication, skipping tenant context setup");
            return true;
        }

        Optional<Tenant> tenantOpt = jwtCase(authentication);

        if (tenantOpt.isEmpty()) {
            tenantOpt = sessionCase(authentication, request);
        }

        if (tenantOpt.isEmpty()) {
            log.warn("Tenant not found for authentication: {}", authentication.getName());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid tenant");
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

    private Optional<Tenant> jwtCase(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return tokenService.getTenantIdFromToken(jwt);
        }
        return Optional.empty();
    }

    private Optional<Tenant> sessionCase(Authentication authentication, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (Objects.isNull(session))
            return Optional.empty();

        if (authentication.getPrincipal() instanceof User user) {
            return Optional.ofNullable(user.getTenant());
        }

        String email = authentication.getName();

        return tenantRepository.findByUserEmail(email);
    }

}
