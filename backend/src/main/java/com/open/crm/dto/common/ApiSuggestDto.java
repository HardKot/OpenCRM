package com.open.crm.dto.common;

public record ApiSuggestDto<T>(T[] items) implements ApiResponse {}
