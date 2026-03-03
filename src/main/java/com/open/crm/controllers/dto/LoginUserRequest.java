package com.open.crm.controllers.dto;

public record LoginUserRequest(
    String email,
    String password
) {
    
}
