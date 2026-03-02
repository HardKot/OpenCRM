package com.open.crm.controllers.dto;

import jakarta.validation.constraints.Email;

public record RegisterTenantRequest(
    @Email(message = "Email should be valid")
    String email
) {
    
}
