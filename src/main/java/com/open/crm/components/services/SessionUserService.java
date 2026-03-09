package com.open.crm.components.services;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import com.open.crm.admin.entities.user.User;
import com.open.crm.core.application.ISessionEmployee;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.entities.employee.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionUserService implements ISessionEmployee {
    private final IEmployeeRepository employeeRepository;

    @Override
    public Optional<Employee> getCurrent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User user = (User) principal;

            return Optional.ofNullable(employeeRepository.findById(user.getEntityId()).orElse(null));
        }

        return Optional.empty();
    }

}
