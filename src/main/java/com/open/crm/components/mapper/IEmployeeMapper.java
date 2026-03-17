package com.open.crm.components.mapper;

import com.open.crm.controllers.dto.EmployeeDto;
import com.open.crm.core.entities.employee.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IEmployeeMapper {
  @Mapping(target = "isDeleted", source = "deleted")
  EmployeeDto toDto(Employee entity);

  Employee toEntity(EmployeeDto dto);
}
