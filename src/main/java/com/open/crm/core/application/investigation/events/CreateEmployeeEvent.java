package com.open.crm.core.application.investigation.events;

import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;

public record CreateEmployeeEvent(Employee employee, Author author) {}
