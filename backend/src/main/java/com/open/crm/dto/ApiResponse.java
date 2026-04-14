package com.open.crm.dto;

public sealed interface ApiResponse permits ApplicationErrorDto, EmployeeDto, ApiSuggestDto {}
