package com.open.crm.dto.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.open.crm.dto.common.ApiResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmployeeDto(
    Long id,
    boolean isDeleted,
    String firstname,
    String lastname,
    String patronymic,
    String position,
    String email,
    String phoneNumber)
    implements ApiResponse {}
