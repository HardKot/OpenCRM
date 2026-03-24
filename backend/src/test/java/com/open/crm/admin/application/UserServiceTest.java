package com.open.crm.admin.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.open.crm.admin.application.interfaces.ISecurityGateway;
import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.application.interfaces.IUserRepository;
import com.open.crm.core.application.repositories.IInvestigationLogRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock private IUserRepository userRepository;

  @Mock private ITenantRepository tenantRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private ApplicationEventPublisher eventPublisher;

  @Mock private IInvestigationLogRepository investigationLogRepository;

  @Mock private ISecurityGateway securityGateway;

  @InjectMocks private UserService userService;

  @Test
  public void testLoadUserByUsername_NotFound() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> userService.loadUserByUsername("test@example.com"));
  }
}
