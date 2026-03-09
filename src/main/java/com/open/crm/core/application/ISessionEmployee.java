package com.open.crm.core.application;

import java.util.Optional;

import com.open.crm.core.entities.employee.Employee;

public interface ISessionEmployee {
    Optional<Employee> getCurrent();
}
