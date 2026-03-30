package com.open.crm.apiControllers.dto;

public interface PageResponse<T> {
  long totalElements();

  int totalPages();

  T[] models();

  public record EmployeePageDto(long totalElements, int totalPages, EmployeeDto[] models)
      implements PageResponse<EmployeeDto> {}
}
