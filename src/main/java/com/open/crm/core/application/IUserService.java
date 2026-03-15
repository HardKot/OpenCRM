package com.open.crm.core.application;

import com.open.crm.core.entities.employee.Employee;

public interface IUserService {

    void updateUserEmail(Employee employee, String email);

    void enabledByEmployee(Employee employee);

    void disabledByEmployee(Employee employee);
}
