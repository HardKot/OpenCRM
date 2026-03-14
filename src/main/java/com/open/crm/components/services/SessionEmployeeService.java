package com.open.crm.components.services;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import com.open.crm.admin.entities.user.User;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionEmployeeService {

    private final IEmployeeRepository employeeRepository;

    public Optional<Employee> getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User user = (User) principal;

            return Optional.ofNullable(employeeRepository.findById(user.getEntityId()).orElse(null));
        }

        return Optional.empty();
    }

    public boolean isShowDeleted() {
        return false;
    }

    public Optional<Author> getCurrentAuthor() {
        return getCurrentEmployee().map(employee -> {
            Author author = new Author();
            author.setEntityId(employee.getId());
            author.setEntityName("EMPLOYEE");
            return author;
        });
    }
}
