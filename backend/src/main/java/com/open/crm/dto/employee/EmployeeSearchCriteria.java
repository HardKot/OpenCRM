package com.open.crm.dto.employee;

public record EmployeeSearchCriteria(
    Integer page,
    Integer size,
    String fullname,
    String position,
    String email,
    String phone,
    Boolean isDeleted,
    String sortBy,
    String sortDirection) {}
