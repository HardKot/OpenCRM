package com.open.crm.config;

import com.open.crm.core.application.repositories.IClientRepository;
import com.open.crm.core.application.repositories.ICommodityCategoryRepository;
import com.open.crm.core.application.repositories.ICommodityRepository;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.application.services.SelectorData;
import com.open.crm.core.entities.employee.Employee;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SelectorsBeans {

  @Bean("employeeSelectorData")
  public SelectorData<Employee> employeeSelectorData(IEmployeeRepository employeeRepository) {

    return new SelectorData<>(employeeRepository);
  }

  @Bean("clientSelectorData")
  public SelectorData<?> clientSelectorData(IClientRepository clientRepository) {
    return new SelectorData<>(clientRepository);
  }

  @Bean("commoditySelectorData")
  public SelectorData<?> commoditySelectorData(ICommodityRepository commodityRepository) {
    return new SelectorData<>(commodityRepository);
  }

  @Bean("commodityCategorySelectorData")
  public SelectorData<?> commodityCategorySelectorData(
      ICommodityCategoryRepository commodityCategoryRepository) {
    return new SelectorData<>(commodityCategoryRepository);
  }
}
