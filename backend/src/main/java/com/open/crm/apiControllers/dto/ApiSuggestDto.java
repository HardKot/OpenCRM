package com.open.crm.apiControllers.dto;

public record ApiSuggestDto<T>(T[] items) implements ApiResponse {}
