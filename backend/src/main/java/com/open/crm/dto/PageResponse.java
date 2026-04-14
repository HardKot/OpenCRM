package com.open.crm.dto;

public interface PageResponse<T extends Object> {
  long totalElements();

  int totalPages();

  T[] models();

  String message();

  Boolean success();

  public record EmployeePageDto(long totalElements, int totalPages, EmployeeDto[] models)
      implements PageResponse<EmployeeDto> {
    public String message() {
      return "";
    }

    public Boolean success() {
      return true;
    }
  }

  public record ErrorPageDto(String message) implements PageResponse<Object> {
    public long totalElements() {
      return 0;
    }

    public int totalPages() {
      return 0;
    }

    public Object[] models() {
      return new Object[0];
    }

    public Boolean success() {
      return false;
    }
  }
}
