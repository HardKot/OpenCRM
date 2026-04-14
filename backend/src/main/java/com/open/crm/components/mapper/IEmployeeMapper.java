package com.open.crm.components.mapper;

import com.open.crm.admin.entities.user.User;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.dto.EmployeeDto;
import com.open.crm.dto.EmployeeUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IEmployeeMapper {
  @Mapping(target = "isDeleted", source = "deleted")
  EmployeeDto toDto(Employee entity);

  Employee toEntity(EmployeeDto dto);

  @Mapping(target = "employee", source = "entity")
  @Mapping(target = "permissions", source = "user.permissions")
  @Mapping(target = "isAccessAllowed", expression = "java(user != null && user.isEnabled())")
  @Mapping(target = "role", source = "user.role")
  EmployeeUserDto toFormDto(Employee entity, User user);
}
