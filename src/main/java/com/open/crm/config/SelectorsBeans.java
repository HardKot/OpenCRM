package com.open.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.crm.core.application.repositories.IClientRepository;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.application.services.SelectorData;
import com.open.crm.core.entities.employee.Employee;

@Configuration
public class SelectorsBeans {

    @Bean("employeeSelectorData")
    public SelectorData<Employee> employeeSelectorData(
            IEmployeeRepository employeeRepository) {

        return new SelectorData<>(employeeRepository);
    }

    @Bean("clientSelectorData")
    public SelectorData<?> clientSelectorData(
            IClientRepository clientRepository) {
        return new SelectorData<>(clientRepository);
    }
}
