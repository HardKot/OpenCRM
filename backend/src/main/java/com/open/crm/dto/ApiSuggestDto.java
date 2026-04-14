package com.open.crm.dto;

public record ApiSuggestDto<T>(T[] items) implements ApiResponse {}
