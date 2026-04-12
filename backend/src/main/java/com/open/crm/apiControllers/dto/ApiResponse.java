package com.open.crm.apiControllers.dto;

public sealed interface ApiResponse permits ApplicationErrorDto, EmployeeDto, ApiSuggestDto {}
