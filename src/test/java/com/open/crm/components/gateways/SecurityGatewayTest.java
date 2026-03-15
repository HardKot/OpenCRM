package com.open.crm.components.gateways;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.open.crm.admin.entities.user.User;
import com.open.crm.security.TokenService;

@ExtendWith(MockitoExtension.class)
public class SecurityGatewayTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SecurityGateway securityGateway;

    @Test
    public void testRefreshAccessUser() {
        User user = new User();
        securityGateway.refreshAccessUser(user);
        verify(tokenService).addRefreshUserTokens(user);
    }
}