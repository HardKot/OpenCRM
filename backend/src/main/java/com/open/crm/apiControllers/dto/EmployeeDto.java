package com.open.crm.apiControllers.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
