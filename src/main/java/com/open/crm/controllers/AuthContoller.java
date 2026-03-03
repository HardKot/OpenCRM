package com.open.crm.controllers;

import org.checkerframework.checker.units.qual.t;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.crm.controllers.dto.LoginUserRequest;
import com.open.crm.controllers.dto.LoginUserResponse;
import com.open.crm.controllers.dto.RegisterTenantRequest;
import com.open.crm.controllers.dto.RegisterTenantResponse;
import com.open.crm.security.IUserRepository;
import com.open.crm.security.TokenService;
import com.open.crm.security.User;
import com.open.crm.security.UserService;
import com.open.crm.tenancy.Tenant;
import com.open.crm.tenancy.TenantContext;
import com.open.crm.tenancy.TenantService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthContoller {
    private final TenantService tenantService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final IUserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

        
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByEmail(authentication.getName()).orElse(null);
            
            Tenant tenant = user.getTenant();
            Jwt accessJwt = tokenService.generateAccessToken(user);
            Jwt refreshJwt = tokenService.generateRefreshToken(user);

            TenantContext.setCurrentTenant(tenant.getId());            
            LoginUserResponse response = new LoginUserResponse(true, user.getId(), user.getTenant().getId(), "Login successful", accessJwt.getTokenValue(), refreshJwt.getTokenValue());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginUserResponse response = new LoginUserResponse(false, null, null, e.getMessage(), null, null);
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/register/tenant")
    public ResponseEntity<RegisterTenantResponse> registerTenant(@RequestBody RegisterTenantRequest request) {
        try {
            tenantService.createTenant(request.email());
            return ResponseEntity.ok(new RegisterTenantResponse(true, "Tenant registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new RegisterTenantResponse(false, e.getMessage()));
        }
    }

}
