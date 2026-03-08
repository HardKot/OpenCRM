package com.open.crm.controllers;

import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.crm.admin.application.UseCreateTenant;
import com.open.crm.admin.application.interfaces.IUserRepository;
import com.open.crm.admin.entities.user.User;
import com.open.crm.controllers.dto.LoginUserRequest;
import com.open.crm.controllers.dto.LoginUserResponse;
import com.open.crm.controllers.dto.RegisterTenantRequest;
import com.open.crm.controllers.dto.RegisterTenantResponse;
import com.open.crm.security.TokenData;
import com.open.crm.security.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UseCreateTenant useCreateTenant;

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final IUserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userRepository.findByEmail(authentication.getName()).orElse(null);

            TokenData tokens = tokenService.generateTokenPairs(user);

            LoginUserResponse response = new LoginUserResponse(
                    true,
                    user.getId(),
                    user.getTenant().getId(),
                    "Login successful",
                    tokens.accessToken(),
                    tokens.refreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while logging in", e);
            LoginUserResponse response = new LoginUserResponse(false, null, null, e.getMessage(), null, null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginUserResponse> refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String tokenValue = extractTokenValue(token);

            TokenData tokens = tokenService.refreshTokne(tokenValue);
            return ResponseEntity.ok(new LoginUserResponse(
                    true,
                    null,
                    null,
                    "Token refreshed successfully",
                    tokens.accessToken(),
                    tokens.refreshToken()));

        } catch (Exception e) {
            log.error("Error occurred while logging in", e);
            LoginUserResponse response = new LoginUserResponse(false, null, null, e.getMessage(), null, null);
            return ResponseEntity.badRequest().body(response);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String tokenValue = extractTokenValue(token);
            if (Objects.isNull(tokenValue)) {
                return ResponseEntity.badRequest().build();
            }

            Jwt jwt = tokenService.decodeToken(tokenValue);
            tokenService.blockToken(jwt);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error occurred while logging out", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register/tenant")
    public ResponseEntity<RegisterTenantResponse> registerTenant(@RequestBody RegisterTenantRequest request) {
        try {
            useCreateTenant.execute(new UseCreateTenant.Params(request.email()));
            return ResponseEntity.ok(new RegisterTenantResponse(true, "Tenant registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new RegisterTenantResponse(false, e.getMessage()));
        }
    }

    private String extractTokenValue(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        String token = header.replace("Bearer ", "");
        return token;
    }
}
