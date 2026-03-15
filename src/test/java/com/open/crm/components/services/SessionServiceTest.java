package com.open.crm.components.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.open.crm.admin.entities.user.User;
import com.open.crm.admin.entities.user.UserEntity;
import com.open.crm.components.errors.SessionException;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.entities.employee.Employee;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private IEmployeeRepository employeeRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testGetUser_Success() {
        User user = new User();
        when(authentication.getPrincipal()).thenReturn(user);

        User result = sessionService.getUser();
        assertEquals(user, result);
    }

    @Test
    public void testGetUser_Unauthorized() {
        when(authentication.getPrincipal()).thenReturn("not a user");

        assertThrows(SessionException.class, () -> sessionService.getUser());
    }

    @Test
    public void testGetEmployee_Success() {
        User user = new User();
        user.setEntityId(1L);
        user.setEntityName(UserEntity.EMPLOYEE);
        Employee employee = new Employee();

        when(authentication.getPrincipal()).thenReturn(user);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = sessionService.getEmployee();
        assertEquals(employee, result);
    }

    @Test
    public void testGetEmployee_NotEmployee() {
        User user = new User();
        user.setEntityName(null);

        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(SessionException.class, () -> sessionService.getEmployee());
    }

    @Test
    public void testGetEmployee_NotFound() {
        User user = new User();
        user.setEntityId(1L);
        user.setEntityName(UserEntity.EMPLOYEE);

        when(authentication.getPrincipal()).thenReturn(user);
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SessionException.class, () -> sessionService.getEmployee());
    }
}