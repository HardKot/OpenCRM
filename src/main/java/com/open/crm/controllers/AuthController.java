package com.open.crm.controllers;

import com.open.crm.admin.application.UserService;
import java.util.Objects;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.crm.admin.application.UseCreateTenant;
import com.open.crm.admin.application.exceptions.UserException;
import com.open.crm.admin.application.interfaces.IUserRepository;
import com.open.crm.admin.entities.user.PasswordType;
import com.open.crm.admin.entities.user.User;
import com.open.crm.components.services.SessionService;
import com.open.crm.controllers.dto.ApplicationErrorDto;
import com.open.crm.controllers.dto.ChangePasswordDto;
import com.open.crm.controllers.dto.LoginUserRequest;
import com.open.crm.controllers.dto.LoginUserResponse;
import com.open.crm.controllers.dto.TokenLoginUserResponse;
import com.open.crm.controllers.dto.RegisterTenantRequest;
import com.open.crm.controllers.dto.RegisterTenantResponse;
import com.open.crm.security.TokenData;
import com.open.crm.security.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    private final UseCreateTenant useCreateTenant;

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final IUserRepository userRepository;
    private final SessionService sessionEmployeeService;

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> actionLogin(@RequestBody LoginUserRequest entity,
            HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(entity.email(), entity.password()));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        LoginUserResponse loginResponse = new LoginUserResponse(true, user.getId(), user.getTenant().getId(),
                user.getPermissions(),
                user.getEntityId(), user.getEntityName(), user.getRole());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/password/level")
    public String actionGetPasswordLevel(@RequestBody String dto) {
        PasswordType passwordType = userService.getPasswordType(dto);
        return passwordType.name();

    }

    @PostMapping("/password")
    public ResponseEntity<LoginUserResponse> actionChangePassword(@RequestBody ChangePasswordDto dto)
            throws UserException {
        User user = sessionEmployeeService.getUser();

        if (!userService.matchPassword(dto.password(), user)) {
            throw new UserException("Current password is incorrect");
        }

        user = userService.updatePassword(user, dto.newPassword());

        LoginUserResponse loginResponse = new LoginUserResponse(true, user.getId(), user.getTenant().getId(),
                user.getPermissions(),
                user.getEntityId(), user.getEntityName(), user.getRole());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/token/login")
    public ResponseEntity<TokenLoginUserResponse> actionTokenLogin(@RequestBody LoginUserRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);

        TokenData tokens = tokenService.generateTokenPairs(user);

        TokenLoginUserResponse response = new TokenLoginUserResponse(user.getId(), user.getTenant().getId(),
                tokens.accessToken().getTokenValue(), tokens.refreshToken().getTokenValue(),
                user.getPermissions(),
                user.getEntityId(), user.getEntityName(), user.getRole());
        return ResponseEntity.ok(response);

    }

    @PostMapping("/token/refresh")
    public ResponseEntity<TokenLoginUserResponse> actionTokenRefresh(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String tokenValue = extractTokenValue(token);

        TokenData tokens = tokenService.refreshTokne(tokenValue);

        TokenLoginUserResponse response = new TokenLoginUserResponse(tokens.userId(), tokens.tenantId(),
                tokens.accessToken().getTokenValue(), tokens.refreshToken().getTokenValue(), tokens.permissions(),
                tokens.entityId(), tokens.entityName(), tokens.role());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> actionLogout(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            HttpServletRequest request, HttpServletResponse response) {
        String tokenValue = extractTokenValue(token);
        if (Objects.nonNull(tokenValue)) {
            Jwt jwt = tokenService.decodeToken(tokenValue);
            tokenService.blockToken(jwt);
        }

        HttpSession session = request.getSession(false);
        if (Objects.nonNull(session)) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();

    }

    @PostMapping("/register/tenant")
    public ResponseEntity<RegisterTenantResponse> registerTenant(@RequestBody RegisterTenantRequest request) {
        useCreateTenant.execute(new UseCreateTenant.Params(request.email()));
        return ResponseEntity.ok(new RegisterTenantResponse(true, "Tenant registered successfully"));
    }

    @ExceptionHandler({
            UserException.class
    })
    public ResponseEntity<ApplicationErrorDto> handleEmployeeException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApplicationErrorDto(ex.getMessage()));
    }

    private String extractTokenValue(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        String token = header.replace("Bearer ", "");
        return token;
    }

}
