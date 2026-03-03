package com.open.crm.security;

import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.open.crm.tenancy.Tenant;
import com.open.crm.tenancy.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final IUserRepository userRepository;
    private final PasswordService passwordService;  

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public User createUser(String email, Tenant tenant, long employeeId) {
        User user = new User();
        user.setEmail(email);
        user.setTenant(tenant);
        user.setEmployeeId(employeeId);
        user.setPassword("");
        return userRepository.save(user);
    }

    public User updatePassword(User user, String password) {
        user.setPassword(passwordService.passwordHash(password));
        return userRepository.save(user);
    }
}
