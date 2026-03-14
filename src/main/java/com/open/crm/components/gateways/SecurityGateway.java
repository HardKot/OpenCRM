package com.open.crm.components.gateways;

import org.springframework.stereotype.Service;

import com.open.crm.admin.application.interfaces.ISecurityGateway;
import com.open.crm.admin.entities.user.User;
import com.open.crm.security.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityGateway implements ISecurityGateway {
    private final TokenService tokenService;

    @Override
    public void refreshAccessUser(User user) {
        tokenService.addRefreshUserTokens(user);
    }
}
